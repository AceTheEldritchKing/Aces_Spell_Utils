package net.acetheeldritchking.aces_spell_utils.client.impactframe;

import net.minecraft.util.Mth;

public final class ImpactFrameTimeline {
    private int brightColor;
    private int darkColor;
    private float intensity;
    private float threshold;
    private float aberrationStrength;
    private int durationTicks;
    private int elapsedTicks;
    private boolean active;

    private int normalEndTick;
    private int invertEndTick;
    private int normalAgainEndTick;

    // flickerTicks is the length of each of the three flicker phases, not the whole flicker.
    public void start(int brightColor, int darkColor, float intensity, float threshold, int durationTicks, int flickerTicks, float aberrationStrength) {
        this.brightColor = brightColor;
        this.darkColor = darkColor;
        this.intensity = Float.isNaN(intensity) ? 0f : Mth.clamp(intensity, 0f, 1f);
        this.threshold = Float.isNaN(threshold) ? 0.5f : Mth.clamp(threshold, 0f, 1f);
        this.aberrationStrength = Float.isNaN(aberrationStrength) ? 0f : Mth.clamp(aberrationStrength, 0f, 1f);
        this.durationTicks = Mth.clamp(durationTicks, 1, 20 * 60);
        this.elapsedTicks = 0;
        this.active = true;

        int perPhase = Mth.clamp(flickerTicks, 1, Math.max(1, this.durationTicks / 3));
        this.normalEndTick = perPhase;
        this.invertEndTick = perPhase * 2;
        this.normalAgainEndTick = perPhase * 3;
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

    public float threshold() {
        return threshold;
    }

    public boolean invert() {
        return elapsedTicks >= normalEndTick && elapsedTicks < invertEndTick;
    }

    public float currentIntensity() {
        if (elapsedTicks < normalAgainEndTick) {
            return intensity;
        }
        float fadeStart = (float) normalAgainEndTick / (float) durationTicks;
        if (fadeStart >= 1f) {
            // Duration is too short to fit a distinct fade phase; the flash has already run its course.
            return 0f;
        }
        float progress = progress();
        float fadeProgress = Mth.clamp((progress - fadeStart) / (1f - fadeStart), 0f, 1f);
        return intensity * (1f - fadeProgress);
    }

    public float currentAberrationStrength() {
        if (intensity <= 0f) {
            return 0f;
        }
        return aberrationStrength * (currentIntensity() / intensity);
    }

    private float progress() {
        return Mth.clamp((float) elapsedTicks / (float) durationTicks, 0f, 1f);
    }
}
