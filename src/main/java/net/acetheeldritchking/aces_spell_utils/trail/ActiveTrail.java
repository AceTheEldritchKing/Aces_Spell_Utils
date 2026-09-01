package net.acetheeldritchking.aces_spell_utils.trail;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class ActiveTrail {
    private final Entity entity;
    private final TrailConfig config;
    private Vec3 lastPos;

    public ActiveTrail(Entity entity, TrailConfig config) {
        this.entity = entity;
        this.config = config;
        this.lastPos = entity.position();
    }

    public Entity entity() {
        return entity;
    }

    public TrailConfig config() {
        return config;
    }

    public Vec3 lastPos() {
        return lastPos;
    }

    public void setLastPos(Vec3 pos) {
        this.lastPos = pos;
    }
}
