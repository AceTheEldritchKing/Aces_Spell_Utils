package net.acetheeldritchking.aces_spell_utils.client.chromaticaberration;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import net.acetheeldritchking.aces_spell_utils.AcesSpellUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import java.io.IOException;

public final class ChromaticAberrationEffect {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation CHAIN = ResourceLocation.fromNamespaceAndPath(AcesSpellUtils.MOD_ID, "shaders/chromatic_aberration.json");

    private static final ChromaticAberrationTimeline TIMELINE = new ChromaticAberrationTimeline();

    private static PostChain chain;
    private static boolean failed;
    private static int chainWidth;
    private static int chainHeight;

    private ChromaticAberrationEffect() {
    }

    public static void trigger(float strength, int durationTicks) {
        TIMELINE.start(strength, durationTicks);
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
        if (!TIMELINE.isActive() || failed) {
            return;
        }
        RenderTarget main = Minecraft.getInstance().getMainRenderTarget();
        if (!ensureChain(main)) {
            return;
        }
        chain.setUniform("Strength", TIMELINE.currentStrength());
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
            LOGGER.error("Aces Spell Utils chromatic aberration chain failed to load, effect disabled", e);
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
