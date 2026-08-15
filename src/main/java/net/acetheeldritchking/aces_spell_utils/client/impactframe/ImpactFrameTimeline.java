package net.acetheeldritchking.aces_spell_utils.client.impactframe;

import net.minecraft.util.Mth;

public final class ImpactFrameTimeline {
    // Flash phases as a share of total duration: normal, inverted, normal again, then fade
    private static final float NORMAL_SHARE = 0.15f;
    private static final float INVERT_SHARE = 0.15f;
    private static final float NORMAL_AGAIN_SHARE = 0.15f;

    private int brightColor;
    private int darkColor;
    private float intensity;
    private int durationTicks;
    private int elapsedTicks;
    private boolean active;

    public void start(int brightColor, int darkColor, float intensity, int durationTicks) {
        this.brightColor = brightColor;
        this.darkColor = darkColor;
        this.intensity = Float.isNaN(intensity) ? 0f : Mth.clamp(intensity, 0f, 1f);
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

    public int brightColor() {
        return brightColor;
    }

    public int darkColor() {
        return darkColor;
    }

    public boolean invert() {
        return elapsedTicks >= normalEndTick() && elapsedTicks < invertEndTick();
    }

    public float currentIntensity() {
        int fadeStartTick = normalAgainEndTick();
        if (elapsedTicks < fadeStartTick) {
            return intensity;
        }
        float fadeStart = (float) fadeStartTick / (float) durationTicks;
        if (fadeStart >= 1f) {
            // Duration is too short to fit a distinct fade phase; the flash has already run its course.
            return 0f;
        }
        float progress = progress();
        float fadeProgress = Mth.clamp((progress - fadeStart) / (1f - fadeStart), 0f, 1f);
        return intensity * (1f - fadeProgress);
    }

    private float progress() {
        return Mth.clamp((float) elapsedTicks / (float) durationTicks, 0f, 1f);
    }

    // Phase boundaries in whole ticks, each phase floored at 1 tick so short durations still
    // show a real normal -> invert -> normal-again sequence instead of skipping the invert phase.
    private int normalEndTick() {
        return Math.max(1, Math.round(NORMAL_SHARE * durationTicks));
    }

    private int invertEndTick() {
        return Math.max(normalEndTick() + 1, Math.round((NORMAL_SHARE + INVERT_SHARE) * durationTicks));
    }

    private int normalAgainEndTick() {
        return Math.max(invertEndTick() + 1, Math.round((NORMAL_SHARE + INVERT_SHARE + NORMAL_AGAIN_SHARE) * durationTicks));
    }
}
