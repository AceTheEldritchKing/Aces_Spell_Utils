package net.acetheeldritchking.aces_spell_utils.dome;

import net.acetheeldritchking.aces_spell_utils.ribbon.ColorRamp;
import net.acetheeldritchking.aces_spell_utils.ribbon.Curve;
import net.acetheeldritchking.aces_spell_utils.ribbon.Easing;
import net.minecraft.util.Mth;

// how the shell itself looks; colour and alpha are sampled across the dome's life, 0 at spawn to 1 at expiry
public record DomeShell(ColorRamp color, Curve alpha, float rimPower, float crownFade) {
    public DomeShell {
        // the exponent sets how thick the visible band is, since the silhouette itself never moves
        rimPower = Float.isFinite(rimPower) ? Mth.clamp(rimPower, 0.1F, 16.0F) : 2.25F;
        // how much dimmer the crown runs than the base, where zero lights the shell evenly
        crownFade = Float.isFinite(crownFade) ? Mth.clamp(crownFade, 0.0F, 1.0F) : 0.25F;
        if (color == null) {
            color = ColorRamp.of(0xFF2020);
        }
        if (alpha == null) {
            alpha = new Curve(0.0F, 1.0F, Easing.LINEAR);
        }
    }

    public static DomeShell of(int color) {
        return new DomeShell(ColorRamp.of(color), new Curve(0.0F, 1.0F, Easing.LINEAR), 2.25F, 0.25F);
    }
}
