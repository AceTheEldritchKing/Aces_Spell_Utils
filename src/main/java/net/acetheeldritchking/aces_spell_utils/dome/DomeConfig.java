package net.acetheeldritchking.aces_spell_utils.dome;

import net.acetheeldritchking.aces_spell_utils.ribbon.Easing;
import net.minecraft.util.Mth;

// radius is in blocks, and the shell expands until settleAt before holding that size for the rest of its life
public record DomeConfig(float radius, int durationTicks, float settleAt, Easing growth,
                         DomeShell shell, DomePulse pulse, DomeWarp warp) {
    public DomeConfig {
        radius = Float.isFinite(radius) ? Mth.clamp(radius, 0.5F, 128.0F) : 8.0F;
        durationTicks = Mth.clamp(durationTicks, 1, 20 * 60);
        // fraction of the life spent expanding, so the rest is held at full size
        settleAt = Float.isFinite(settleAt) ? Mth.clamp(settleAt, 0.01F, 1.0F) : 0.18F;
        if (growth == null) {
            growth = Easing.EASE_OUT_QUAD;
        }
        if (shell == null) {
            shell = DomeShell.of(0xFF2020);
        }
        if (pulse == null) {
            pulse = DomePulse.of();
        }
        if (warp == null) {
            warp = DomeWarp.of();
        }
    }

    public static DomeConfig of(int color, float radius, int durationTicks) {
        return new DomeConfig(radius, durationTicks, 0.18F, Easing.EASE_OUT_QUAD,
                DomeShell.of(color), DomePulse.of(), DomeWarp.of());
    }
}
