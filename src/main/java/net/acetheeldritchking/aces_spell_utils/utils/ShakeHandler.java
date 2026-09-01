package net.acetheeldritchking.aces_spell_utils.utils;

import net.acetheeldritchking.aces_spell_utils.network.TriggerShakePacket;
import net.acetheeldritchking.aces_spell_utils.shake.ShakeConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

public final class ShakeHandler {
    private ShakeHandler() {
    }

    // only players inside the radius can feel it, so the send is bounded by the same number rather than a fixed range
    public static void trigger(ServerLevel level, Vec3 pos, ShakeConfig config) {
        if (level == null || pos == null || config == null) {
            return;
        }
        PacketDistributor.sendToPlayersNear(level, null, pos.x, pos.y, pos.z, config.radius(), new TriggerShakePacket(pos, config));
    }

    public static void trigger(Entity source, ShakeConfig config) {
        if (source == null || source.isRemoved() || !(source.level() instanceof ServerLevel level)) {
            return;
        }
        trigger(level, source.position(), config);
    }
}
