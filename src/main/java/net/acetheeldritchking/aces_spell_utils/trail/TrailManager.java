package net.acetheeldritchking.aces_spell_utils.trail;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApiStatus.Internal
public final class TrailManager {
    // ConcurrentHashMap so a client-side render-thread lookup can never race the server tick thread's iterator
    private static final Map<UUID, ActiveTrail> ACTIVE = new ConcurrentHashMap<>();

    private TrailManager() {
    }

    public static void attach(Entity entity, TrailConfig config) {
        ACTIVE.put(entity.getUUID(), new ActiveTrail(entity, config));
    }

    public static void detach(Entity entity) {
        ACTIVE.remove(entity.getUUID());
    }

    public static boolean isAttached(Entity entity) {
        ActiveTrail trail = ACTIVE.get(entity.getUUID());
        return trail != null && trail.entity() == entity;
    }

    public static void clear() {
        ACTIVE.clear();
    }

    public static void tick() {
        if (ACTIVE.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<UUID, ActiveTrail>> iterator = ACTIVE.entrySet().iterator();
        while (iterator.hasNext()) {
            ActiveTrail trail = iterator.next().getValue();
            Entity entity = trail.entity();
            if (!entity.isAlive()) {
                iterator.remove();
                continue;
            }
            emit(trail, entity);
        }
    }

    private static void emit(ActiveTrail trail, Entity entity) {
        TrailConfig config = trail.config();
        Vec3 from = trail.lastPos();
        Vec3 to = entity.position();
        double distSq = from.distanceToSqr(to);
        // a teleport is not movement, so restart the trail instead of drawing a line across the world
        if (distSq > 1024.0) {
            trail.setLastPos(to);
            return;
        }
        if (distSq < 1.0E-6) {
            MagicManager.spawnParticles(entity.level(), config.particle(), to.x, to.y + config.yOffset(), to.z,
                    config.density(), config.spread(), config.spread(), config.spread(), config.speed(), false);
            trail.setLastPos(to);
            return;
        }
        // step along the movement so a fast entity leaves a line, not a dotted trail
        for (int i = 0; i < config.density(); i++) {
            Vec3 at = from.lerp(to, (i + 1.0) / config.density());
            MagicManager.spawnParticles(entity.level(), config.particle(),
                    at.x,
                    at.y + config.yOffset(),
                    at.z,
                    1,
                    config.spread(),
                    config.spread(),
                    config.spread(),
                    config.speed(),
                    false);
        }
        trail.setLastPos(to);
    }
}
