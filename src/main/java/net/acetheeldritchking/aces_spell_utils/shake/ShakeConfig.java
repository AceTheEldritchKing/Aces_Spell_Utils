package net.acetheeldritchking.aces_spell_utils.shake;

import net.minecraft.util.Mth;

// radius is in blocks and magnitude is in degrees of camera swing at the epicentre
public record ShakeConfig(float radius, float magnitude, int durationTicks, int fadeTicks) {
    public ShakeConfig {
        radius = Float.isFinite(radius) ? Mth.clamp(radius, 1.0F, 256.0F) : 16.0F;
        magnitude = Float.isFinite(magnitude) ? Mth.clamp(magnitude, 0.0F, 20.0F) : 2.0F;
        durationTicks = Mth.clamp(durationTicks, 0, 20 * 60);
        // the fade is its own span after the hold, and a zero would make the tail divide by nothing
        fadeTicks = Mth.clamp(fadeTicks, 1, 20 * 60);
    }

    public static ShakeConfig of(float radius, float magnitude, int durationTicks) {
        return new ShakeConfig(radius, magnitude, durationTicks, 8);
    }
}
