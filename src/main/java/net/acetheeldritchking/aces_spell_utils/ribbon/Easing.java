package net.acetheeldritchking.aces_spell_utils.ribbon;

// Ordinals cross the network, so new constants go on the end and existing ones are never reordered
public enum Easing {
    LINEAR {
        @Override
        public float ease(float t) {
            return t;
        }
    },
    EASE_IN_QUAD {
        @Override
        public float ease(float t) {
            return t * t;
        }
    },
    EASE_OUT_QUAD {
        @Override
        public float ease(float t) {
            return t * (2.0F - t);
        }
    },
    EASE_IN_OUT_QUAD {
        @Override
        public float ease(float t) {
            return t < 0.5F ? 2.0F * t * t : -1.0F + (4.0F - 2.0F * t) * t;
        }
    },
    EASE_IN_CUBIC {
        @Override
        public float ease(float t) {
            return t * t * t;
        }
    },
    EASE_OUT_CUBIC {
        @Override
        public float ease(float t) {
            float f = t - 1.0F;
            return f * f * f + 1.0F;
        }
    };

    private static final Easing[] VALUES = values();

    public abstract float ease(float t);

    // an unknown ordinal means a newer sender, and a cosmetic packet must not kill the connection
    public static Easing byOrdinal(int ordinal) {
        return ordinal >= 0 && ordinal < VALUES.length ? VALUES[ordinal] : LINEAR;
    }
}
