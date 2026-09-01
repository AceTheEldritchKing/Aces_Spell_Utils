package net.acetheeldritchking.aces_spell_utils.dome;

import net.minecraft.util.Mth;

// the breathing the shell does once it has settled, which thins and thickens the band without resizing it
public record DomePulse(float amount, float speed) {
    public DomePulse {
        // swings the rim exponent either side of its base
        amount = Float.isFinite(amount) ? Mth.clamp(amount, 0.0F, 4.0F) : 0.18F;
        speed = Float.isFinite(speed) ? Mth.clamp(speed, 0.0F, 8.0F) : 0.8F;
    }

    public static DomePulse of() {
        return new DomePulse(0.18F, 0.8F);
    }

    public static DomePulse none() {
        return new DomePulse(0.0F, 0.0F);
    }
}
