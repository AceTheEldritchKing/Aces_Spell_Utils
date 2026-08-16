package net.acetheeldritchking.aces_spell_utils.utils;

import net.acetheeldritchking.aces_spell_utils.network.TriggerImpactFramePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Collection;
import java.util.List;

public final class ImpactFrameHandler {
    public static final float DEFAULT_THRESHOLD = 0.5f;
    public static final int DEFAULT_FLICKER_TICKS = 3;

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
        trigger(players, brightColor, darkColor, intensity, DEFAULT_THRESHOLD, durationTicks, DEFAULT_FLICKER_TICKS);
    }

    public static void trigger(ServerPlayer player, int brightColor, int darkColor, float intensity, float threshold, int durationTicks, int flickerTicks) {
        trigger(List.of(player), brightColor, darkColor, intensity, threshold, durationTicks, flickerTicks);
    }

    public static void trigger(Collection<ServerPlayer> players, int brightColor, int darkColor, float intensity, float threshold, int durationTicks, int flickerTicks) {
        float clampedIntensity = Mth.clamp(intensity, 0f, 1f);
        float clampedThreshold = Mth.clamp(threshold, 0f, 1f);
        int clampedDuration = Math.max(1, durationTicks);
        int clampedFlickerTicks = Math.max(1, flickerTicks);
        TriggerImpactFramePacket packet = new TriggerImpactFramePacket(brightColor, darkColor, clampedIntensity, clampedThreshold, clampedDuration, clampedFlickerTicks);
        for (ServerPlayer player : players) {
            PacketDistributor.sendToPlayer(player, packet);
        }
    }
}
