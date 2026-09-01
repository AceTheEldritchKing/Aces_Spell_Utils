package net.acetheeldritchking.aces_spell_utils.roar;

// Ordinals cross the network and index the shader's Style uniform, so new constants go on the end and existing ones are never reordered
public enum RoarStyle {
    ZOOM,
    RING;

    private static final RoarStyle[] VALUES = values();

    public static RoarStyle byOrdinal(int ordinal) {
        return ordinal >= 0 && ordinal < VALUES.length ? VALUES[ordinal] : ZOOM;
    }
}
