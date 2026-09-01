package net.acetheeldritchking.aces_spell_utils.client.particle;

import net.acetheeldritchking.aces_spell_utils.particles.TrailParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import org.joml.Vector3f;

public class TrailParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected TrailParticle(ClientLevel level, double x, double y, double z, TrailParticleOptions options, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;
        Vector3f color = options.getColor();
        this.rCol = color.x();
        this.gCol = color.y();
        this.bCol = color.z();
        this.quadSize = options.getScale() * 0.15F;
        this.lifetime = 14 + this.random.nextInt(6);
        this.friction = 0.92F;
        this.hasPhysics = false;
        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
        this.alpha = 1.0F - (float) this.age / (float) this.lifetime;
        this.quadSize *= 0.96F;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<TrailParticleOptions> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(TrailParticleOptions options, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            TrailParticle particle = new TrailParticle(level, x, y, z, options, this.sprites);
            particle.setParticleSpeed(xSpeed, ySpeed, zSpeed);
            return particle;
        }
    }
}
