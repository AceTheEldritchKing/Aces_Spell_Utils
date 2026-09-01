package net.acetheeldritchking.aces_spell_utils.network;

import net.acetheeldritchking.aces_spell_utils.AcesSpellUtils;
import net.acetheeldritchking.aces_spell_utils.client.chromaticaberration.ChromaticAberrationEffect;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class TriggerChromaticAberrationPacket implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<TriggerChromaticAberrationPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AcesSpellUtils.MOD_ID, "trigger_chromatic_aberration"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TriggerChromaticAberrationPacket> STREAM_CODEC = CustomPacketPayload.codec(TriggerChromaticAberrationPacket::write, TriggerChromaticAberrationPacket::new);

    private final float strength;
    private final int durationTicks;

    public TriggerChromaticAberrationPacket(float strength, int durationTicks) {
        this.strength = strength;
        this.durationTicks = durationTicks;
    }

    public TriggerChromaticAberrationPacket(FriendlyByteBuf buf) {
        strength = buf.readFloat();
        durationTicks = buf.readInt();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeFloat(strength);
        buf.writeInt(durationTicks);
    }

    public static void handle(TriggerChromaticAberrationPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ChromaticAberrationEffect.trigger(packet.strength, packet.durationTicks));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
