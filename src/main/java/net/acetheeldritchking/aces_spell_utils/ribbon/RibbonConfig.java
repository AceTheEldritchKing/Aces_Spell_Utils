package net.acetheeldritchking.aces_spell_utils.ribbon;

import net.minecraft.util.Mth;

// length counts tick samples, so it sets how long the ribbon lingers rather than how long it is in blocks
public record RibbonConfig(ColorRamp color, Curve width, Curve alpha, int length, boolean additive) {
    public RibbonConfig {
        if (color == null) {
            color = ColorRamp.of(0xFFFFFF);
        }
        if (width == null) {
            width = Curve.of(0.18F);
        }
        // an unbounded width can overflow head - tail to infinity and yield NaN vertex positions, poisoning the whole batch
        width = new Curve(Mth.clamp(width.head(), 0.0F, 4.0F), Mth.clamp(width.tail(), 0.0F, 4.0F), width.easing());
        if (alpha == null) {
            alpha = Curve.of(1.0F);
        }
        length = Mth.clamp(length, 2, 128);
    }

    public static RibbonConfig of(int color, float width, int length) {
        return new RibbonConfig(ColorRamp.of(color), Curve.of(width), Curve.of(1.0F), length, false);
    }
}
