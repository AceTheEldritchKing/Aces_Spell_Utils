package net.acetheeldritchking.aces_spell_utils.events;

import io.redspace.ironsspellbooks.render.ClientStaffItemExtensions;
import io.redspace.ironsspellbooks.util.MinecraftInstanceHelper;
import net.acetheeldritchking.aces_spell_utils.AcesSpellUtils;
import net.acetheeldritchking.aces_spell_utils.client.chromaticaberration.ChromaticAberrationEffect;
import net.acetheeldritchking.aces_spell_utils.client.dome.DomeWarpEffect;
import net.acetheeldritchking.aces_spell_utils.client.dome.DomeManager;
import net.acetheeldritchking.aces_spell_utils.client.dome.DomeRenderer;
import net.acetheeldritchking.aces_spell_utils.client.impactframe.ImpactFrameEffect;
import net.acetheeldritchking.aces_spell_utils.client.ribbon.RibbonManager;
import net.acetheeldritchking.aces_spell_utils.client.shake.ShakeManager;
import net.acetheeldritchking.aces_spell_utils.client.ribbon.RibbonRenderer;
import net.acetheeldritchking.aces_spell_utils.client.roar.RoarEffect;
import net.acetheeldritchking.aces_spell_utils.items.weapons.MagicGunItem;
import net.acetheeldritchking.aces_spell_utils.registries.ExampleItemRegistry;
import net.acetheeldritchking.aces_spell_utils.utils.ASUtils;
import net.acetheeldritchking.aces_spell_utils.utils.boss_music.BossMusicManager;
import net.acetheeldritchking.aces_spell_utils.utils.boss_music.UniqueBossMusicManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
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
        RibbonManager.clear();
        DomeManager.clear();
        DomeWarpEffect.closeChain();
        ShakeManager.clear();
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

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event)
    {
        ImpactFrameEffect.tick();
        ChromaticAberrationEffect.tick();
        RibbonManager.tick();
        RoarEffect.tick();
        DomeManager.tick();
        ShakeManager.tick();
    }

    // Runs after the hand is drawn, before the HUD, so the flash covers the held item too
    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Pre event)
    {
        ImpactFrameEffect.process(event.getPartialTick().getGameTimeDeltaPartialTick(false));
        ChromaticAberrationEffect.process(event.getPartialTick().getGameTimeDeltaPartialTick(false));
        RoarEffect.process(event.getPartialTick().getGameTimeDeltaPartialTick(false));
        DomeWarpEffect.process(event.getPartialTick().getGameTimeDeltaPartialTick(false));
    }

    // three decorrelated rates so pitch, yaw and roll never line up into a circular wobble
    @SubscribeEvent
    public static void onComputeCameraAngles(ViewportEvent.ComputeCameraAngles event)
    {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null)
        {
            return;
        }
        float partialTick = (float) event.getPartialTick();
        float amplitude = ShakeManager.amplitude(player.getEyePosition(partialTick), partialTick);
        if (amplitude <= 0.0F)
        {
            return;
        }
        float time = player.tickCount + partialTick;
        event.setPitch(event.getPitch() + amplitude * Mth.cos(time * 3.0F + 2.0F));
        event.setYaw(event.getYaw() + amplitude * Mth.cos(time * 5.0F + 1.0F));
        event.setRoll(event.getRoll() + amplitude * Mth.cos(time * 4.0F));
    }

    // AFTER_ENTITIES so ribbons sort with entities and stay occluded by terrain
    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event)
    {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES)
        {
            RibbonRenderer.render(event.getPoseStack(), event.getCamera().getPosition(), event.getPartialTick().getGameTimeDeltaPartialTick(false));
            DomeRenderer.render(event.getPoseStack(), event.getCamera().getPosition(), event.getPartialTick().getGameTimeDeltaPartialTick(false));
            RoarEffect.captureMatrices(event.getProjectionMatrix(), event.getModelViewMatrix(), event.getCamera().getPosition());
            DomeWarpEffect.captureMatrices(event.getProjectionMatrix(), event.getModelViewMatrix(), event.getCamera().getPosition());
        }
    }
}
