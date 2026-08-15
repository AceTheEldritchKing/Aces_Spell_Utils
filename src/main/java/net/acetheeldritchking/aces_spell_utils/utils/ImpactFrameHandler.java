package net.acetheeldritchking.aces_spell_utils.utils;

import net.acetheeldritchking.aces_spell_utils.network.TriggerImpactFramePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Collection;
import java.util.List;

public final class ImpactFrameHandler {
    private ImpactFrameHandler() {
    }

    // Flashes the player's screen white/black by default, mapped from scene luminance
    public static void trigger(ServerPlayer player, int brightColor, float intensity, int durationTicks) {
        trigger(player, brightColor, 0x000000, intensity, durationTicks);
    }

    public static void trigger(ServerPlayer player, int brightColor, int darkColor, float intensity, int durationTicks) {
        trigger(List.of(player), brightColor, darkColor, intensity, durationTicks);
    }

    public static void trigger(Collection<ServerPlayer> players, int brightColor, int darkColor, float intensity, int durationTicks) {
        float clampedIntensity = Mth.clamp(intensity, 0f, 1f);
        int clampedDuration = Math.max(1, durationTicks);
        TriggerImpactFramePacket packet = new TriggerImpactFramePacket(brightColor, darkColor, clampedIntensity, clampedDuration);
        for (ServerPlayer player : players) {
            PacketDistributor.sendToPlayer(player, packet);
        }
    }
}
