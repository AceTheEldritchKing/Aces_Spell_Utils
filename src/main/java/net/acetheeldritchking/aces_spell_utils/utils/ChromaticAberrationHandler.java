package net.acetheeldritchking.aces_spell_utils.utils;

import net.acetheeldritchking.aces_spell_utils.network.TriggerChromaticAberrationPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Collection;
import java.util.List;

public final class ChromaticAberrationHandler {
    private ChromaticAberrationHandler() {
    }

    public static void trigger(ServerPlayer player, float strength, int durationTicks) {
        trigger(List.of(player), strength, durationTicks);
    }

    public static void trigger(Collection<ServerPlayer> players, float strength, int durationTicks) {
        float clampedStrength = Mth.clamp(strength, 0f, 1f);
        int clampedDuration = Math.max(1, durationTicks);
        TriggerChromaticAberrationPacket packet = new TriggerChromaticAberrationPacket(clampedStrength, clampedDuration);
        for (ServerPlayer player : players) {
            PacketDistributor.sendToPlayer(player, packet);
        }
    }
}
