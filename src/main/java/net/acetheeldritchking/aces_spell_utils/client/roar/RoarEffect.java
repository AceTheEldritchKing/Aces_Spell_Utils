package net.acetheeldritchking.aces_spell_utils.client.roar;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import net.acetheeldritchking.aces_spell_utils.AcesSpellUtils;
import net.acetheeldritchking.aces_spell_utils.roar.RoarConfig;
import net.acetheeldritchking.aces_spell_utils.roar.RoarTimeline;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.slf4j.Logger;

import java.io.IOException;

@ApiStatus.Internal
public final class RoarEffect {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation CHAIN = ResourceLocation.fromNamespaceAndPath(AcesSpellUtils.MOD_ID, "shaders/roar.json");

    private static final double NEAR_SOURCE_SQR = 2.25;

    private static final RoarTimeline TIMELINE = new RoarTimeline();

    private static PostChain chain;
    private static boolean failed;
    private static int chainWidth;
    private static int chainHeight;
    private static int sourceId = -1;
    private static Vec3 sourcePos;

    private static Matrix4f projection;
    private static Matrix4f modelView;
    private static Vec3 cameraPos;

    private RoarEffect() {
    }

    public static void trigger(int entityId, RoarConfig config) {
        sourceId = entityId;
        sourcePos = null;
        TIMELINE.start(config);
    }

    // a roar with no entity behind it, so the centre stays on the spot it was fired from
    public static void trigger(Vec3 pos, RoarConfig config) {
        sourceId = -1;
        sourcePos = pos;
        TIMELINE.start(config);
    }

    // the post pass runs at gui time where the camera matrices are gone, so they are kept from the level pass
    public static void captureMatrices(Matrix4f projectionMatrix, Matrix4f modelViewMatrix, Vec3 camera) {
        projection = projectionMatrix;
        modelView = modelViewMatrix;
        cameraPos = camera;
    }

    // Called once per client tick
    public static void tick() {
        TIMELINE.tick();
        if (!TIMELINE.isActive()) {
            closeChain();
        }
    }

    // Called once per rendered frame, after the level finishes rendering
    public static void process(float partialTick) {
        if (!TIMELINE.isActive() || failed || projection == null || modelView == null || cameraPos == null) {
            return;
        }
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        Vec3 world = sourcePos;
        if (world == null) {
            Entity source = level.getEntity(sourceId);
            if (source == null) {
                return;
            }
            world = source.getPosition(partialTick).add(0.0, source.getBbHeight() * 0.5, 0.0);
        }
        Vec3 relative = world.subtract(cameraPos);
        float centerX;
        float centerY;
        if (relative.lengthSqr() < NEAR_SOURCE_SQR) {
            // the camera sits inside the source, where the projection is unstable and the roar is on top of the viewer anyway
            centerX = 0.5F;
            centerY = 0.5F;
        } else {
            Vector4f clip = new Matrix4f(projection).mul(modelView).transform(new Vector4f(
                    (float) relative.x, (float) relative.y, (float) relative.z, 1.0F));
            // behind the camera the divide still yields a finite but mirrored point, so skip instead of drawing it
            if (clip.w() <= 0.0F) {
                return;
            }
            // a source near the camera plane divides by almost zero, so the centre is pinned within reach of the screen
            centerX = Mth.clamp((clip.x() / clip.w()) * 0.5F + 0.5F, -1.0F, 2.0F);
            centerY = Mth.clamp((clip.y() / clip.w()) * 0.5F + 0.5F, -1.0F, 2.0F);
        }

        RenderTarget main = Minecraft.getInstance().getMainRenderTarget();
        if (!ensureChain(main)) {
            return;
        }
        RoarConfig config = TIMELINE.config();
        chain.setUniform("CenterX", centerX);
        chain.setUniform("CenterY", centerY);
        chain.setUniform("Style", (float) config.style().ordinal());
        chain.setUniform("Strength", config.strength());
        chain.setUniform("Sharpness", config.sharpness());
        chain.setUniform("Radius", TIMELINE.radius(partialTick));
        chain.setUniform("Thickness", config.thickness());
        chain.setUniform("Blur", config.blur());
        chain.setUniform("Refraction", config.refraction());
        chain.setUniform("Aspect", (float) main.width / (float) main.height);
        chain.setUniform("Intensity", TIMELINE.intensity(partialTick));
        // Matches vanilla's own GameRenderer.render reset before its post-effect call
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.resetTextureMatrix();
        chain.process(partialTick);
        main.bindWrite(true);
    }

    private static boolean ensureChain(RenderTarget main) {
        try {
            if (chain == null) {
                chain = new PostChain(Minecraft.getInstance().getTextureManager(), Minecraft.getInstance().getResourceManager(), main, CHAIN);
                chain.resize(main.width, main.height);
                chainWidth = main.width;
                chainHeight = main.height;
            } else if (chainWidth != main.width || chainHeight != main.height) {
                chain.resize(main.width, main.height);
                chainWidth = main.width;
                chainHeight = main.height;
            }
            return true;
        } catch (IOException | RuntimeException e) {
            failed = true;
            LOGGER.error("Aces Spell Utils roar chain failed to load, effect disabled", e);
            return false;
        }
    }

    private static void closeChain() {
        if (chain != null) {
            chain.close();
            chain = null;
        }
    }
}
