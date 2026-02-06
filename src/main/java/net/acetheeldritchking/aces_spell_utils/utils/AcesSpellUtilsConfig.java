package net.acetheeldritchking.aces_spell_utils.utils;

import net.acetheeldritchking.aces_spell_utils.AcesSpellUtils;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = AcesSpellUtils.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class AcesSpellUtilsConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // Mana steal draining mana
    private static final ModConfigSpec.BooleanValue MANA_STEAL_DRAINS_MANA = BUILDER
            .comment("Defines whether or not mana steal should drain the mana of the target entity. Default is true")
            .define("Mana Steal drains mana", true);

    // If one domain is x times as refined as another, then it will automatically win in a clash
    private static final ModConfigSpec.ConfigValue<Double> REFINEMENT_DIFFERENCE = BUILDER
            .comment("Defines the minimum refinement ratio for a domain to automatically overwhelm another domain in a clash. Default is 1.5, must be above 1.")
            .define("Refinement victory factor", 1.5D);

    // Dev mode
    private static final ModConfigSpec.BooleanValue DEV_MODE = BUILDER
            .comment("Whether or not dev mode should be active - this means all loggers will be visible. Default is false")
            .define("Enable Dev mode", false);

    public static final ModConfigSpec SPEC = BUILDER.build();
    public static boolean manaStealDrain;
    public static double refinementDifference;
    public static boolean devMode;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        manaStealDrain = MANA_STEAL_DRAINS_MANA.get();
        refinementDifference = REFINEMENT_DIFFERENCE.get();
        devMode = DEV_MODE.get();
    }
}
