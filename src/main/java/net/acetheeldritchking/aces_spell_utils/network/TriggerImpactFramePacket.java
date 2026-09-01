package net.acetheeldritchking.aces_spell_utils.network;

import net.acetheeldritchking.aces_spell_utils.AcesSpellUtils;
import net.acetheeldritchking.aces_spell_utils.client.impactframe.ImpactFrameEffect;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class TriggerImpactFramePacket implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<TriggerImpactFramePacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AcesSpellUtils.MOD_ID, "trigger_impact_frame"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TriggerImpactFramePacket> STREAM_CODEC = CustomPacketPayload.codec(TriggerImpactFramePacket::write, TriggerImpactFramePacket::new);

    private final int brightColor;
    private final int darkColor;
    private final float intensity;
    private final float threshold;
    private final int durationTicks;
    private final int flickerTicks;
    private final float aberrationStrength;

    public TriggerImpactFramePacket(int brightColor, int darkColor, float intensity, float threshold, int durationTicks, int flickerTicks, float aberrationStrength) {
        this.brightColor = brightColor;
        this.darkColor = darkColor;
        this.intensity = intensity;
        this.threshold = threshold;
        this.durationTicks = durationTicks;
        this.flickerTicks = flickerTicks;
        this.aberrationStrength = aberrationStrength;
    }

    public TriggerImpactFramePacket(FriendlyByteBuf buf) {
        brightColor = buf.readInt();
        darkColor = buf.readInt();
        intensity = buf.readFloat();
        threshold = buf.readFloat();
        durationTicks = buf.readInt();
        flickerTicks = buf.readInt();
        aberrationStrength = buf.readFloat();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(brightColor);
        buf.writeInt(darkColor);
        buf.writeFloat(intensity);
        buf.writeFloat(threshold);
        buf.writeInt(durationTicks);
        buf.writeInt(flickerTicks);
        buf.writeFloat(aberrationStrength);
    }

    public static void handle(TriggerImpactFramePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ImpactFrameEffect.trigger(packet.brightColor, packet.darkColor, packet.intensity, packet.threshold, packet.durationTicks, packet.flickerTicks, packet.aberrationStrength));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
