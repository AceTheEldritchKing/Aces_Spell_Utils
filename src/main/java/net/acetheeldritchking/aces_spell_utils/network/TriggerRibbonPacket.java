package net.acetheeldritchking.aces_spell_utils.network;

import io.netty.handler.codec.DecoderException;
import net.acetheeldritchking.aces_spell_utils.AcesSpellUtils;
import net.acetheeldritchking.aces_spell_utils.client.ribbon.RibbonManager;
import net.acetheeldritchking.aces_spell_utils.ribbon.ColorRamp;
import net.acetheeldritchking.aces_spell_utils.ribbon.Curve;
import net.acetheeldritchking.aces_spell_utils.ribbon.Easing;
import net.acetheeldritchking.aces_spell_utils.ribbon.RibbonConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class TriggerRibbonPacket implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<TriggerRibbonPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AcesSpellUtils.MOD_ID, "trigger_ribbon"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TriggerRibbonPacket> STREAM_CODEC = CustomPacketPayload.codec(TriggerRibbonPacket::write, TriggerRibbonPacket::new);

    private final int entityId;
    private final boolean attach;
    private final RibbonConfig config;

    public TriggerRibbonPacket(int entityId, boolean attach, RibbonConfig config) {
        this.entityId = entityId;
        this.attach = attach;
        this.config = config;
    }

    public TriggerRibbonPacket(FriendlyByteBuf buf) {
        entityId = buf.readInt();
        attach = buf.readBoolean();
        config = readConfig(buf);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeBoolean(attach);
        writeConfig(buf, config);
    }

    private static void writeConfig(FriendlyByteBuf buf, RibbonConfig config) {
        int[] colors = config.color().colors();
        buf.writeVarInt(colors.length);
        for (int color : colors) {
            buf.writeInt(color);
        }
        buf.writeByte(config.color().easing().ordinal());
        writeCurve(buf, config.width());
        writeCurve(buf, config.alpha());
        buf.writeVarInt(config.length());
        buf.writeBoolean(config.additive());
    }

    private static RibbonConfig readConfig(FriendlyByteBuf buf) {
        int count = buf.readVarInt();
        // no valid sender writes a count outside this range, so treat it as a malformed packet
        if (count < 1 || count > ColorRamp.MAX_STOPS) {
            throw new DecoderException("Ribbon colour stop count out of range: " + count);
        }
        int[] colors = new int[count];
        for (int i = 0; i < count; i++) {
            colors[i] = buf.readInt();
        }
        ColorRamp ramp = new ColorRamp(colors, Easing.byOrdinal(buf.readByte()));
        Curve width = readCurve(buf);
        Curve alpha = readCurve(buf);
        int length = buf.readVarInt();
        boolean additive = buf.readBoolean();
        return new RibbonConfig(ramp, width, alpha, length, additive);
    }

    private static void writeCurve(FriendlyByteBuf buf, Curve curve) {
        buf.writeFloat(curve.head());
        buf.writeFloat(curve.tail());
        buf.writeByte(curve.easing().ordinal());
    }

    private static Curve readCurve(FriendlyByteBuf buf) {
        float head = buf.readFloat();
        float tail = buf.readFloat();
        Easing easing = Easing.byOrdinal(buf.readByte());
        return new Curve(head, tail, easing);
    }

    public static void handle(TriggerRibbonPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (packet.attach) {
                RibbonManager.attach(packet.entityId, packet.config);
            } else {
                RibbonManager.detach(packet.entityId);
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
