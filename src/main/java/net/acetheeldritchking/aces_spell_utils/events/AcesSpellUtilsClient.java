package net.acetheeldritchking.aces_spell_utils.events;

import io.redspace.ironsspellbooks.render.ClientStaffItemExtensions;
import io.redspace.ironsspellbooks.util.MinecraftInstanceHelper;
import net.acetheeldritchking.aces_spell_utils.AcesSpellUtils;
import net.acetheeldritchking.aces_spell_utils.items.weapons.MagicGunItem;
import net.acetheeldritchking.aces_spell_utils.registries.ExampleItemRegistry;
import net.acetheeldritchking.aces_spell_utils.utils.ASUtils;
import net.acetheeldritchking.aces_spell_utils.utils.boss_music.BossMusicManager;
import net.acetheeldritchking.aces_spell_utils.utils.boss_music.UniqueBossMusicManager;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

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
        UniqueBossMusicManager.hardStop();
    }

    @SubscribeEvent
    public static void itemTooltipsEvents(ItemTooltipEvent event)
    {
        ItemStack stack = event.getItemStack();

        MinecraftInstanceHelper.ifPlayerPresent((player) ->
        {
            var localPlayer = (LocalPlayer) player;
            var lines = event.getToolTip();
            boolean advanced = event.getFlags().isAdvanced();

            // Gun spell tooltip
            if (stack.getItem() instanceof MagicGunItem)
            {
                ASUtils.handleCastingImplementTooltip(stack, localPlayer, lines, advanced);
            }
        });
    }
}
