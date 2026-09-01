package net.acetheeldritchking.aces_spell_utils.network;

import net.acetheeldritchking.aces_spell_utils.AcesSpellUtils;
import net.acetheeldritchking.aces_spell_utils.client.roar.RoarEffect;
import net.acetheeldritchking.aces_spell_utils.ribbon.Easing;
import net.acetheeldritchking.aces_spell_utils.roar.RoarConfig;
import net.acetheeldritchking.aces_spell_utils.roar.RoarStyle;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class TriggerRoarPacket implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<TriggerRoarPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AcesSpellUtils.MOD_ID, "trigger_roar"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TriggerRoarPacket> STREAM_CODEC = CustomPacketPayload.codec(TriggerRoarPacket::write, TriggerRoarPacket::new);

    // a negative id means the roar is pinned to a world position instead of following an entity
    private static final int NO_ENTITY = -1;

    private final int entityId;
    private final Vec3 pos;
    private final RoarConfig config;

    public TriggerRoarPacket(int entityId, RoarConfig config) {
        this.entityId = entityId;
        this.pos = Vec3.ZERO;
        this.config = config;
    }

    public TriggerRoarPacket(Vec3 pos, RoarConfig config) {
        this.entityId = NO_ENTITY;
        this.pos = pos;
        this.config = config;
    }

    public TriggerRoarPacket(FriendlyByteBuf buf) {
        entityId = buf.readInt();
        pos = entityId < 0 ? new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()) : Vec3.ZERO;
        RoarStyle style = RoarStyle.byOrdinal(buf.readByte());
        float strength = buf.readFloat();
        float sharpness = buf.readFloat();
        float radius = buf.readFloat();
        float thickness = buf.readFloat();
        float blur = buf.readFloat();
        float refraction = buf.readFloat();
        int durationTicks = buf.readVarInt();
        Easing growth = Easing.byOrdinal(buf.readByte());
        config = new RoarConfig(style, strength, sharpness, radius, thickness, blur, refraction, durationTicks, growth);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        if (entityId < 0) {
            buf.writeDouble(pos.x);
            buf.writeDouble(pos.y);
            buf.writeDouble(pos.z);
        }
        buf.writeByte(config.style().ordinal());
        buf.writeFloat(config.strength());
        buf.writeFloat(config.sharpness());
        buf.writeFloat(config.radius());
        buf.writeFloat(config.thickness());
        buf.writeFloat(config.blur());
        buf.writeFloat(config.refraction());
        buf.writeVarInt(config.durationTicks());
        buf.writeByte(config.growth().ordinal());
    }

    public static void handle(TriggerRoarPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (packet.entityId < 0) {
                RoarEffect.trigger(packet.pos, packet.config);
            } else {
                RoarEffect.trigger(packet.entityId, packet.config);
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
