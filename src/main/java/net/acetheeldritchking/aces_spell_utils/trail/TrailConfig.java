package net.acetheeldritchking.aces_spell_utils.trail;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;

// density is how many particles get spaced along the entity's movement each tick
public record TrailConfig(ParticleOptions particle, int density, float yOffset, float spread, float speed) {
    public TrailConfig {
        density = Mth.clamp(density, 1, 16);
        spread = Math.max(0.0F, spread);
        speed = Math.max(0.0F, speed);
    }

    public static TrailConfig of(ParticleOptions particle, int density) {
        return new TrailConfig(particle, density, 0.0F, 0.0F, 0.0F);
    }
}
