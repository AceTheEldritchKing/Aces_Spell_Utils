package net.acetheeldritchking.aces_spell_utils.utils;

import net.acetheeldritchking.aces_spell_utils.trail.TrailConfig;
import net.acetheeldritchking.aces_spell_utils.trail.TrailManager;
import net.minecraft.world.entity.Entity;

public final class TrailHandler {
    private TrailHandler() {
    }

    // Trails run until the entity dies or detach is called, since a projectile's flight time is not known up front
    public static void attach(Entity entity, TrailConfig config) {
        if (entity == null || entity.isRemoved() || entity.level().isClientSide()) {
            return;
        }
        TrailManager.attach(entity, config);
    }

    public static void detach(Entity entity) {
        if (entity == null || entity.level().isClientSide()) {
            return;
        }
        TrailManager.detach(entity);
    }

    public static boolean isAttached(Entity entity) {
        return entity != null && !entity.level().isClientSide() && TrailManager.isAttached(entity);
    }
}
