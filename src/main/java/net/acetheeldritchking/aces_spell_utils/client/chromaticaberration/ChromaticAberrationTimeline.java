package net.acetheeldritchking.aces_spell_utils.client.chromaticaberration;

import net.minecraft.util.Mth;

public final class ChromaticAberrationTimeline {
    private static final float HOLD_FRACTION = 0.5f;

    private float strength;
    private int durationTicks;
    private int elapsedTicks;
    private boolean active;

    public void start(float strength, int durationTicks) {
        this.strength = Float.isNaN(strength) ? 0f : Math.max(0f, strength);
        this.durationTicks = Mth.clamp(durationTicks, 1, 20 * 60);
        this.elapsedTicks = 0;
        this.active = true;
    }

    public void tick() {
        if (!active) {
            return;
        }
        elapsedTicks++;
        if (elapsedTicks >= durationTicks) {
            active = false;
        }
    }

    public boolean isActive() {
        return active;
    }

    public float currentStrength() {
        float progress = Mth.clamp((float) elapsedTicks / (float) durationTicks, 0f, 1f);
        if (progress < HOLD_FRACTION) {
            return strength;
        }
        float fadeProgress = Mth.clamp((progress - HOLD_FRACTION) / (1f - HOLD_FRACTION), 0f, 1f);
        return strength * (1f - fadeProgress);
    }
}
