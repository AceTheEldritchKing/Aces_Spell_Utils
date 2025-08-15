package net.acetheeldritchking.aces_spell_utils.network;

import net.acetheeldritchking.aces_spell_utils.AcesSpellUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class AddShaderEffectPacket implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<AddShaderEffectPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AcesSpellUtils.MOD_ID, "add_shader_effect"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AddShaderEffectPacket> STREAM_CODEC = CustomPacketPayload.codec(AddShaderEffectPacket::write, AddShaderEffectPacket::new);
    private final String modid;
    private final String location;


    public AddShaderEffectPacket(String modid, String location) {
        this.modid = modid;
        this.location = location;
    }

    public AddShaderEffectPacket(FriendlyByteBuf buf) {
        modid = buf.readUtf();
        location = buf.readUtf();
    }


    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(modid);
        buf.writeUtf(location);
    }

    public static void handle(AddShaderEffectPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            PayloadHandler.handleClientBoundShaderEffect(packet.modid,packet.location);
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
