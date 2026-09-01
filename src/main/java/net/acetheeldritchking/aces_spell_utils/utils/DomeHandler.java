package net.acetheeldritchking.aces_spell_utils.utils;

import net.acetheeldritchking.aces_spell_utils.dome.DomeConfig;
import net.acetheeldritchking.aces_spell_utils.network.TriggerDomePacket;
import net.acetheeldritchking.aces_spell_utils.shake.ShakeConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

public final class DomeHandler {
    // wider than the roar's range, since a dome may be up to 256 blocks across and still visible from well outside it
    private static final double RANGE = 256.0;

    private static final float SHAKE_REACH = 3.0F;
    private static final float SHAKE_MAGNITUDE = 1.6F;
    private static final int SHAKE_FADE = 10;
    // the kick reads as the moment of impact, so it stays short no matter how long the shell lingers afterwards
    private static final int SHAKE_MAX_TICKS = 12;

    private DomeHandler() {
    }

    public static void trigger(ServerLevel level, Vec3 pos, DomeConfig config) {
        if (level == null || pos == null || config == null) {
            return;
        }
        PacketDistributor.sendToPlayersNear(level, null, pos.x, pos.y, pos.z, RANGE, new TriggerDomePacket(pos, config));
        // the camera kick reaches well past the shell itself, which is what sells the blast as having weight
        ShakeHandler.trigger(level, pos, new ShakeConfig(config.radius() * SHAKE_REACH, SHAKE_MAGNITUDE,
                Math.min(SHAKE_MAX_TICKS, Math.max(1, config.durationTicks() / 3)), SHAKE_FADE));
    }
}
