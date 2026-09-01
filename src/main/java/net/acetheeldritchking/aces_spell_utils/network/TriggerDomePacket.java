package net.acetheeldritchking.aces_spell_utils.network;

import io.netty.handler.codec.DecoderException;
import net.acetheeldritchking.aces_spell_utils.AcesSpellUtils;
import net.acetheeldritchking.aces_spell_utils.client.dome.DomeManager;
import net.acetheeldritchking.aces_spell_utils.dome.DomeConfig;
import net.acetheeldritchking.aces_spell_utils.dome.DomePulse;
import net.acetheeldritchking.aces_spell_utils.dome.DomeShell;
import net.acetheeldritchking.aces_spell_utils.dome.DomeWarp;
import net.acetheeldritchking.aces_spell_utils.ribbon.ColorRamp;
import net.acetheeldritchking.aces_spell_utils.ribbon.Curve;
import net.acetheeldritchking.aces_spell_utils.ribbon.Easing;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class TriggerDomePacket implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<TriggerDomePacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AcesSpellUtils.MOD_ID, "trigger_dome"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TriggerDomePacket> STREAM_CODEC = CustomPacketPayload.codec(TriggerDomePacket::write, TriggerDomePacket::new);

    private final Vec3 pos;
    private final DomeConfig config;

    public TriggerDomePacket(Vec3 pos, DomeConfig config) {
        this.pos = pos;
        this.config = config;
    }

    public TriggerDomePacket(FriendlyByteBuf buf) {
        pos = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        config = readConfig(buf);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeDouble(pos.x);
        buf.writeDouble(pos.y);
        buf.writeDouble(pos.z);
        writeConfig(buf, config);
    }

    // each block below writes one record in its declared field order, so the reader can mirror it one for one
    private static void writeConfig(FriendlyByteBuf buf, DomeConfig config) {
        buf.writeFloat(config.radius());
        buf.writeVarInt(config.durationTicks());
        buf.writeFloat(config.settleAt());
        buf.writeByte(config.growth().ordinal());
        writeShell(buf, config.shell());
        writePulse(buf, config.pulse());
        writeWarp(buf, config.warp());
    }

    private static DomeConfig readConfig(FriendlyByteBuf buf) {
        float radius = buf.readFloat();
        int durationTicks = buf.readVarInt();
        float settleAt = buf.readFloat();
        Easing growth = Easing.byOrdinal(buf.readByte());
        return new DomeConfig(radius, durationTicks, settleAt, growth, readShell(buf), readPulse(buf), readWarp(buf));
    }

    private static void writeShell(FriendlyByteBuf buf, DomeShell shell) {
        int[] colors = shell.color().colors();
        buf.writeVarInt(colors.length);
        for (int color : colors) {
            buf.writeInt(color);
        }
        buf.writeByte(shell.color().easing().ordinal());
        buf.writeFloat(shell.alpha().head());
        buf.writeFloat(shell.alpha().tail());
        buf.writeByte(shell.alpha().easing().ordinal());
        buf.writeFloat(shell.rimPower());
        buf.writeFloat(shell.crownFade());
    }

    private static DomeShell readShell(FriendlyByteBuf buf) {
        int count = buf.readVarInt();
        // no valid sender writes a count outside this range, so treat it as a malformed packet
        if (count < 1 || count > ColorRamp.MAX_STOPS) {
            throw new DecoderException("Dome colour stop count out of range: " + count);
        }
        int[] colors = new int[count];
        for (int i = 0; i < count; i++) {
            colors[i] = buf.readInt();
        }
        ColorRamp ramp = new ColorRamp(colors, Easing.byOrdinal(buf.readByte()));
        Curve alpha = new Curve(buf.readFloat(), buf.readFloat(), Easing.byOrdinal(buf.readByte()));
        return new DomeShell(ramp, alpha, buf.readFloat(), buf.readFloat());
    }

    private static void writePulse(FriendlyByteBuf buf, DomePulse pulse) {
        buf.writeFloat(pulse.amount());
        buf.writeFloat(pulse.speed());
    }

    private static DomePulse readPulse(FriendlyByteBuf buf) {
        return new DomePulse(buf.readFloat(), buf.readFloat());
    }

    private static void writeWarp(FriendlyByteBuf buf, DomeWarp warp) {
        buf.writeFloat(warp.strength());
        buf.writeVarInt(warp.rows());
        buf.writeFloat(warp.density());
        buf.writeFloat(warp.rate());
    }

    private static DomeWarp readWarp(FriendlyByteBuf buf) {
        return new DomeWarp(buf.readFloat(), buf.readVarInt(), buf.readFloat(), buf.readFloat());
    }

    public static void handle(TriggerDomePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> DomeManager.spawn(packet.pos, packet.config));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
