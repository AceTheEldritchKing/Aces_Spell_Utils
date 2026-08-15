package net.acetheeldritchking.aces_spell_utils.client.impactframe;

import net.minecraft.util.Mth;

public final class ImpactFrameTimeline {
    // flash phases as a share of total duration: normal, inverted, normal again, then fade
    private static final float NORMAL_SHARE = 0.15f;
    private static final float INVERT_SHARE = 0.15f;
    private static final float NORMAL_AGAIN_SHARE = 0.15f;
    private static final float FADE_START = NORMAL_SHARE + INVERT_SHARE + NORMAL_AGAIN_SHARE;

    private int brightColor;
    private int darkColor;
    private float intensity;
    private int durationTicks;
    private int elapsedTicks;
    private boolean active;

    public void start(int brightColor, int darkColor, float intensity, int durationTicks) {
        this.brightColor = brightColor;
        this.darkColor = darkColor;
        this.intensity = intensity;
        this.durationTicks = durationTicks;
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

    public int brightColor() {
        return brightColor;
    }

    public int darkColor() {
        return darkColor;
    }

    public boolean invert() {
        float progress = progress();
        return progress >= NORMAL_SHARE && progress < NORMAL_SHARE + INVERT_SHARE;
    }

    public float currentIntensity() {
        float progress = progress();
        if (progress < FADE_START) {
            return intensity;
        }
        float fadeProgress = Mth.clamp((progress - FADE_START) / (1f - FADE_START), 0f, 1f);
        return intensity * (1f - fadeProgress);
    }

    private float progress() {
        return Mth.clamp((float) elapsedTicks / (float) durationTicks, 0f, 1f);
    }
}
