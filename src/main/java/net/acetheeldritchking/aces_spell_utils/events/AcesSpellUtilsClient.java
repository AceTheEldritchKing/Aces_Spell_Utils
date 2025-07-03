package net.acetheeldritchking.aces_spell_utils.events;

import net.acetheeldritchking.aces_spell_utils.AcesSpellUtils;
import net.acetheeldritchking.aces_spell_utils.utils.boss_music.BossMusicManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = AcesSpellUtils.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = AcesSpellUtils.MOD_ID, value = Dist.CLIENT)
public class AcesSpellUtilsClient {
    public AcesSpellUtilsClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        //container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
    }

    @SubscribeEvent
    public static void onPlayerLogout(ClientPlayerNetworkEvent.LoggingOut event)
    {
        BossMusicManager.hardStop();
    }
}
