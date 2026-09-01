package net.acetheeldritchking.aces_spell_utils.client.dome;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import net.acetheeldritchking.aces_spell_utils.AcesSpellUtils;
import net.acetheeldritchking.aces_spell_utils.dome.DomeConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.slf4j.Logger;

import java.io.IOException;

@ApiStatus.Internal
public final class DomeWarpEffect {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation CHAIN = ResourceLocation.fromNamespaceAndPath(AcesSpellUtils.MOD_ID, "shaders/dome_warp.json");

    // each dome costs a full screen pass, so only the nearest few are drawn even though the manager tracks more
    private static final int MAX_PASSES = 4;
    // a camera inside the shell has no finite silhouette, so the lens is pinned to something that still covers the screen
    private static final float ENGULFED_RADIUS = 4.0F;

    private static PostChain chain;
    private static boolean failed;
    private static int chainWidth;
    private static int chainHeight;

    private static Matrix4f projection;
    private static Matrix4f modelView;
    private static Vec3 cameraPos;

    private DomeWarpEffect() {
    }

    // the post pass runs at gui time where the camera matrices are gone, so they are kept from the level pass
    public static void captureMatrices(Matrix4f projectionMatrix, Matrix4f modelViewMatrix, Vec3 camera) {
        projection = projectionMatrix;
        modelView = modelViewMatrix;
        cameraPos = camera;
    }

    // Called once per rendered frame, after the level finishes rendering
    public static void process(float partialTick) {
        if (DomeManager.active().isEmpty() || failed || projection == null || modelView == null || cameraPos == null) {
            return;
        }
        RenderTarget main = Minecraft.getInstance().getMainRenderTarget();
        if (!ensureChain(main)) {
            return;
        }
        float aspect = (float) main.width / (float) main.height;
        int drawn = 0;
        for (ActiveDome dome : DomeManager.active()) {
            if (drawn >= MAX_PASSES) {
                break;
            }
            if (renderOne(dome, main, aspect, partialTick)) {
                drawn++;
            }
        }
    }

    private static boolean renderOne(ActiveDome dome, RenderTarget main, float aspect, float partialTick) {
        DomeConfig config = dome.config();
        float progress = dome.progress(partialTick);
        float alpha = config.shell().alpha().at(progress);
        float worldRadius = dome.radius(partialTick);
        if (alpha <= 0.0F || worldRadius <= 0.0F) {
            return false;
        }

        Vec3 relative = dome.center().subtract(cameraPos);
        Vector4f clip = new Matrix4f(projection).mul(modelView).transform(new Vector4f(
                (float) relative.x, (float) relative.y, (float) relative.z, 1.0F));
        double distance = relative.length();
        boolean engulfed = distance <= worldRadius;
        // behind the camera the divide still yields a finite but mirrored point, so skip unless the shell already surrounds the viewer
        if (clip.w() <= 0.0F && !engulfed) {
            return false;
        }

        float centerX = 0.5F;
        float centerY = 0.5F;
        if (clip.w() > 0.0F) {
            centerX = Mth.clamp((clip.x() / clip.w()) * 0.5F + 0.5F, -2.0F, 3.0F);
            centerY = Mth.clamp((clip.y() / clip.w()) * 0.5F + 0.5F, -2.0F, 3.0F);
        }

        float screenRadius;
        if (engulfed) {
            screenRadius = ENGULFED_RADIUS;
        } else {
            // the sphere subtends asin(r/d), and projection.m11 is 1/tan(halfFov), which converts that angle into screen units
            float tanTheta = (float) Math.tan(Math.asin(worldRadius / distance));
            screenRadius = Mth.clamp(tanTheta * projection.m11() * 0.5F, 1.0E-4F, ENGULFED_RADIUS);
        }

        chain.setUniform("CenterX", centerX);
        chain.setUniform("CenterY", centerY);
        chain.setUniform("Radius", screenRadius);
        chain.setUniform("Strength", config.warp().strength() * alpha);
        chain.setUniform("Rows", (float) config.warp().rows());
        chain.setUniform("Density", config.warp().density());
        // floored inside the shader, so this only has to advance fast enough to keep picking new buckets
        chain.setUniform("Phase", progress * config.durationTicks() * config.warp().rate());
        chain.setUniform("Aspect", aspect);
        // Matches vanilla's own GameRenderer.render reset before its post-effect call
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.resetTextureMatrix();
        chain.process(partialTick);
        main.bindWrite(true);
        return true;
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
            LOGGER.error("Aces Spell Utils dome warp chain failed to load, effect disabled", e);
            return false;
        }
    }

    public static void closeChain() {
        if (chain != null) {
            chain.close();
            chain = null;
        }
    }
}
