package net.acetheeldritchking.aces_spell_utils.ribbon;

import net.minecraft.util.Mth;

// head is the value at the entity end, tail the value at the oldest end
public record Curve(float head, float tail, Easing easing) {
    public Curve {
        head = Float.isFinite(head) ? head : 0.0F;
        tail = Float.isFinite(tail) ? tail : 0.0F;
        if (easing == null) {
            easing = Easing.LINEAR;
        }
    }

    public static Curve of(float head) {
        return new Curve(head, 0.0F, Easing.LINEAR);
    }

    // t runs 0 at the tail to 1 at the head
    public float at(float t) {
        return tail + (head - tail) * easing.ease(Mth.clamp(t, 0.0F, 1.0F));
    }
}
