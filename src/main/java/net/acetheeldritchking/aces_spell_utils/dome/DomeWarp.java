package net.acetheeldritchking.aces_spell_utils.dome;

import net.minecraft.util.Mth;

// the sideways tear the dome puts through the picture, like a signal breaking up
public record DomeWarp(float strength, int rows, float density, float rate) {
    public DomeWarp {
        // how far a torn row slides, as a fraction of screen width
        strength = Float.isFinite(strength) ? Mth.clamp(strength, 0.0F, 0.2F) : 0.012F;
        // scanlines the screen is cut into, where fewer rows means chunkier bands
        rows = Mth.clamp(rows, 1, 1080);
        // fraction of rows torn at once, since a breaking signal leaves most of the picture intact
        density = Float.isFinite(density) ? Mth.clamp(density, 0.0F, 1.0F) : 0.35F;
        // pattern redraws per tick, so the tear snaps to a new arrangement rather than crawling
        rate = Float.isFinite(rate) ? Mth.clamp(rate, 0.0F, 20.0F) : 1.0F;
    }

    public static DomeWarp of() {
        return new DomeWarp(0.012F, 120, 0.35F, 1.0F);
    }

    public static DomeWarp none() {
        return new DomeWarp(0.0F, 120, 0.0F, 1.0F);
    }
}
