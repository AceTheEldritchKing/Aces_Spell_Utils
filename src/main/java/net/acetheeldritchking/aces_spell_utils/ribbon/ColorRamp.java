package net.acetheeldritchking.aces_spell_utils.ribbon;

import net.minecraft.util.Mth;

import java.util.Arrays;

public record ColorRamp(int[] colors, Easing easing) {
    public static final int MAX_STOPS = 8;

    public ColorRamp {
        if (colors == null || colors.length == 0) {
            colors = new int[]{0xFFFFFF};
        } else {
            colors = Arrays.copyOf(colors, Math.min(colors.length, MAX_STOPS));
        }
        if (easing == null) {
            easing = Easing.LINEAR;
        }
    }

    public static ColorRamp of(int solid) {
        return new ColorRamp(new int[]{solid}, Easing.LINEAR);
    }

    // the compact constructor copies on the way in, so the accessor must copy on the way out or callers can mutate the live config
    @Override
    public int[] colors() {
        return colors.clone();
    }

    // t runs 0 at the tail to 1 at the head, so colors[0] is the tail stop
    public int at(float t) {
        if (colors.length == 1) {
            return colors[0];
        }
        float eased = Mth.clamp(easing.ease(Mth.clamp(t, 0.0F, 1.0F)), 0.0F, 1.0F);
        float scaled = eased * (colors.length - 1);
        int index = Math.min((int) scaled, colors.length - 2);
        float f = scaled - index;
        int from = colors[index];
        int to = colors[index + 1];
        return (lerpChannel(f, from, to, 16) << 16) | (lerpChannel(f, from, to, 8) << 8) | lerpChannel(f, from, to, 0);
    }

    private static int lerpChannel(float f, int from, int to, int shift) {
        int a = (from >> shift) & 0xFF;
        int b = (to >> shift) & 0xFF;
        return Mth.clamp(Math.round(a + f * (b - a)), 0, 255);
    }
}
