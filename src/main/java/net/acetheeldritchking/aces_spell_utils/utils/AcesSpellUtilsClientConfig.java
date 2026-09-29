package net.acetheeldritchking.aces_spell_utils.utils;

import net.neoforged.neoforge.common.ModConfigSpec;

public class AcesSpellUtilsClientConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue IMPACT_FRAME_ENABLED;

    public static final ModConfigSpec SPEC;

    static {

        BUILDER.push("VFX");
        BUILDER.push("Shaders");
        BUILDER.comment("Toggle for ASU Impact Frames (will also apply to any dependencies). Default is true.");
        IMPACT_FRAME_ENABLED = BUILDER.define("impact_frame_toggle", true);
        BUILDER.pop();
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
