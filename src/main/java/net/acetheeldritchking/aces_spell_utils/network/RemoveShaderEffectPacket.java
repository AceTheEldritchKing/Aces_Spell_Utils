package net.acetheeldritchking.aces_spell_utils.network;

import net.acetheeldritchking.aces_spell_utils.AcesSpellUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;


public class RemoveShaderEffectPacket implements CustomPacketPayload {
    public static final Type<RemoveShaderEffectPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(AcesSpellUtils.MOD_ID, "remove_shader_effect"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RemoveShaderEffectPacket> STREAM_CODEC = CustomPacketPayload.codec(RemoveShaderEffectPacket::write, RemoveShaderEffectPacket::new);


    public RemoveShaderEffectPacket() {
    }

    public RemoveShaderEffectPacket(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
    }


    public void write(FriendlyByteBuf buf) {

    }

    public static void handle(RemoveShaderEffectPacket packet, IPayloadContext context) {
        context.enqueueWork(PayloadHandler::removeClientBoundShaderEffect);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
