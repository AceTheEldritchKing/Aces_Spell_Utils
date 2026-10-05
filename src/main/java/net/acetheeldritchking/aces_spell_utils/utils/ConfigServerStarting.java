package net.acetheeldritchking.aces_spell_utils.utils;

import net.acetheeldritchking.aces_spell_utils.AcesSpellUtils;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import static net.acetheeldritchking.aces_spell_utils.utils.AcesSpellUtilsConfig.*;

// It has to stay in Utils otherwise it will break everything
// Eventually I'll find a way to put this in the mod class but like
// I am so done rn
@EventBusSubscriber(modid = AcesSpellUtils.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class ConfigServerStarting {
    @SubscribeEvent
    static void onLoad(final ServerStartingEvent event)
    {
        manaStealDrain = MANA_STEAL_DRAINS_MANA.get();
        refinementDifference = REFINEMENT_DIFFERENCE.get();
        devMode = DEV_MODE.get();
        manaRendWhitelist = MANA_REND_WHITELIST.get();
        manaStealWhitelist = MANA_STEAL_WHITELIST.get();
    }
}
