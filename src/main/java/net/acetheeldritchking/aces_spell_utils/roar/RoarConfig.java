package net.acetheeldritchking.aces_spell_utils.roar;

import net.acetheeldritchking.aces_spell_utils.ribbon.Easing;
import net.minecraft.util.Mth;

// radius and thickness are in aspect corrected UV, where centre to vertical edge is 0.5
public record RoarConfig(RoarStyle style, float strength, float sharpness, float radius, float thickness, float blur,
                         float refraction, int durationTicks, Easing growth) {
    public RoarConfig {
        strength = Float.isFinite(strength) ? Mth.clamp(strength, 0.0F, 1.0F) : 0.4F;
        sharpness = Float.isFinite(sharpness) ? Mth.clamp(sharpness, 0.0F, 8.0F) : 1.0F;
        radius = Float.isFinite(radius) ? Mth.clamp(radius, 0.0F, 2.0F) : 1.0F;
        thickness = Float.isFinite(thickness) ? Mth.clamp(thickness, 0.01F, 1.0F) : 0.15F;
        blur = Float.isFinite(blur) ? Mth.clamp(blur, 0.0F, 0.5F) : 0.0F;
        refraction = Float.isFinite(refraction) ? Mth.clamp(refraction, 0.0F, 0.5F) : 0.0F;
        durationTicks = Mth.clamp(durationTicks, 1, 20 * 60);
        if (style == null) {
            style = RoarStyle.ZOOM;
        }
        if (growth == null) {
            growth = Easing.LINEAR;
        }
    }

    // the screen streaks outward from the roaring entity, which is the effect on its own
    public static RoarConfig zoom(float strength, float sharpness, int durationTicks, Easing growth) {
        return new RoarConfig(RoarStyle.ZOOM, strength, sharpness, 0.0F, 0.15F, 0.0F, 0.0F, durationTicks, growth);
    }

    // a band of blur and refraction sweeps outward, leaving the rest of the screen untouched
    public static RoarConfig ring(float radius, float thickness, float blur, float refraction, int durationTicks, Easing growth) {
        return new RoarConfig(RoarStyle.RING, 0.0F, 1.0F, radius, thickness, blur, refraction, durationTicks, growth);
    }


    public static RoarConfig of(float strength, int durationTicks) {
        return zoom(strength, 1.0F, durationTicks, Easing.EASE_OUT_QUAD);
    }
}
