package net.acetheeldritchking.aces_spell_utils.roar;

import net.minecraft.util.Mth;

public final class RoarTimeline {
    private static final float ATTACK_END = 0.1F;
    private static final float FADE_START = 0.66F;

    private RoarConfig config;
    private int elapsedTicks;
    private boolean active;

    public void start(RoarConfig config) {
        this.config = config;
        this.elapsedTicks = 0;
        this.active = true;
    }

    public void tick() {
        if (!active) {
            return;
        }
        elapsedTicks++;
        if (elapsedTicks >= config.durationTicks()) {
            active = false;
        }
    }

    public boolean isActive() {
        return active;
    }

    public RoarConfig config() {
        return config;
    }

    // the partial tick is carried through so the band travels smoothly instead of stepping once per tick
    public float radius(float partialTick) {
        return config.radius() * config.growth().ease(progress(partialTick));
    }

    // a short attack so the effect lands as a hit rather than popping on, then a hold, then a dissipating tail
    public float intensity(float partialTick) {
        float progress = progress(partialTick);
        if (progress < ATTACK_END) {
            return progress / ATTACK_END;
        }
        if (progress < FADE_START) {
            return 1.0F;
        }
        return 1.0F - (progress - FADE_START) / (1.0F - FADE_START);
    }

    private float progress(float partialTick) {
        return Mth.clamp(((float) elapsedTicks + partialTick) / (float) config.durationTicks(), 0.0F, 1.0F);
    }
}
