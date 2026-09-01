package net.acetheeldritchking.aces_spell_utils.network;

import net.acetheeldritchking.aces_spell_utils.AcesSpellUtils;
import net.acetheeldritchking.aces_spell_utils.client.shake.ShakeManager;
import net.acetheeldritchking.aces_spell_utils.shake.ShakeConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class TriggerShakePacket implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<TriggerShakePacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AcesSpellUtils.MOD_ID, "trigger_shake"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TriggerShakePacket> STREAM_CODEC = CustomPacketPayload.codec(TriggerShakePacket::write, TriggerShakePacket::new);

    private final Vec3 pos;
    private final ShakeConfig config;

    public TriggerShakePacket(Vec3 pos, ShakeConfig config) {
        this.pos = pos;
        this.config = config;
    }

    public TriggerShakePacket(FriendlyByteBuf buf) {
        pos = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        config = new ShakeConfig(buf.readFloat(), buf.readFloat(), buf.readVarInt(), buf.readVarInt());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeDouble(pos.x);
        buf.writeDouble(pos.y);
        buf.writeDouble(pos.z);
        buf.writeFloat(config.radius());
        buf.writeFloat(config.magnitude());
        buf.writeVarInt(config.durationTicks());
        buf.writeVarInt(config.fadeTicks());
    }

    public static void handle(TriggerShakePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ShakeManager.spawn(packet.pos, packet.config));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
