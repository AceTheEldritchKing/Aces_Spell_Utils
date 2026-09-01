package net.acetheeldritchking.aces_spell_utils.utils;

import net.acetheeldritchking.aces_spell_utils.network.TriggerRoarPacket;
import net.acetheeldritchking.aces_spell_utils.roar.RoarConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Collection;

public final class RoarHandler {
    private static final double RANGE = 128.0;

    private RoarHandler() {
    }

    // the ring is centred on this entity, so it is the roaring creature rather than the viewer
    public static void trigger(Entity source, RoarConfig config) {
        if (source == null || source.isRemoved() || source.level().isClientSide()) {
            return;
        }
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(source, new TriggerRoarPacket(source.getId(), config));
    }

    // the roar has no entity behind it, so it reaches everyone close enough to the spot to plausibly see it
    public static void trigger(ServerLevel level, Vec3 pos, RoarConfig config) {
        if (level == null || pos == null) {
            return;
        }
        PacketDistributor.sendToPlayersNear(level, null, pos.x, pos.y, pos.z, RANGE, new TriggerRoarPacket(pos, config));
    }

    public static void trigger(Collection<ServerPlayer> players, Entity source, RoarConfig config) {
        if (source == null || source.isRemoved() || source.level().isClientSide()) {
            return;
        }
        TriggerRoarPacket packet = new TriggerRoarPacket(source.getId(), config);
        for (ServerPlayer player : players) {
            PacketDistributor.sendToPlayer(player, packet);
        }
    }
}
