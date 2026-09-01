package net.acetheeldritchking.aces_spell_utils.network;

import net.acetheeldritchking.aces_spell_utils.AcesSpellUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = AcesSpellUtils.MOD_ID)
public class PayloadHandler {
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar payloadRegistrar = event.registrar(AcesSpellUtils.MOD_ID).versioned("4.0.0").optional();


        payloadRegistrar.playToClient(AddShaderEffectPacket.TYPE, AddShaderEffectPacket.STREAM_CODEC, AddShaderEffectPacket::handle);
        payloadRegistrar.playToClient(RemoveShaderEffectPacket.TYPE, RemoveShaderEffectPacket.STREAM_CODEC, RemoveShaderEffectPacket::handle);
        payloadRegistrar.playToClient(TriggerImpactFramePacket.TYPE, TriggerImpactFramePacket.STREAM_CODEC, TriggerImpactFramePacket::handle);
        payloadRegistrar.playToClient(TriggerChromaticAberrationPacket.TYPE, TriggerChromaticAberrationPacket.STREAM_CODEC, TriggerChromaticAberrationPacket::handle);
        payloadRegistrar.playToClient(TriggerRibbonPacket.TYPE, TriggerRibbonPacket.STREAM_CODEC, TriggerRibbonPacket::handle);
        payloadRegistrar.playToClient(TriggerRoarPacket.TYPE, TriggerRoarPacket.STREAM_CODEC, TriggerRoarPacket::handle);
        payloadRegistrar.playToClient(TriggerDomePacket.TYPE, TriggerDomePacket.STREAM_CODEC, TriggerDomePacket::handle);
        payloadRegistrar.playToClient(TriggerShakePacket.TYPE, TriggerShakePacket.STREAM_CODEC, TriggerShakePacket::handle);
    }

    public static void handleClientBoundShaderEffect(String modid, String location){
        Minecraft mc = Minecraft.getInstance();
        GameRenderer render = mc.gameRenderer;
        LocalPlayer clientPlayer = mc.player;
        if(clientPlayer != null) {
            render.loadEffect(ResourceLocation.fromNamespaceAndPath(modid,location));
        }
    }

    public static void removeClientBoundShaderEffect(){
        Minecraft mc = Minecraft.getInstance();
        GameRenderer render = mc.gameRenderer;
        LocalPlayer clientPlayer = mc.player;
        if(clientPlayer != null) {
            render.shutdownEffect();
        }
    }
}