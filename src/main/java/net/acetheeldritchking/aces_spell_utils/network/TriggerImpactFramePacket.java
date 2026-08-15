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
    private final int durationTicks;

    public TriggerImpactFramePacket(int brightColor, int darkColor, float intensity, int durationTicks) {
        this.brightColor = brightColor;
        this.darkColor = darkColor;
        this.intensity = intensity;
        this.durationTicks = durationTicks;
    }

    public TriggerImpactFramePacket(FriendlyByteBuf buf) {
        brightColor = buf.readInt();
        darkColor = buf.readInt();
        intensity = buf.readFloat();
        durationTicks = buf.readInt();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(brightColor);
        buf.writeInt(darkColor);
        buf.writeFloat(intensity);
        buf.writeInt(durationTicks);
    }

    public static void handle(TriggerImpactFramePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ImpactFrameEffect.trigger(packet.brightColor, packet.darkColor, packet.intensity, packet.durationTicks));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
