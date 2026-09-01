package net.acetheeldritchking.aces_spell_utils.particles;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.acetheeldritchking.aces_spell_utils.registries.ASParticleRegistry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ScalableParticleOptionsBase;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class TrailParticleOptions extends ScalableParticleOptionsBase {
    public static final MapCodec<TrailParticleOptions> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    ExtraCodecs.VECTOR3F.fieldOf("color").forGetter(TrailParticleOptions::getColor),
                    SCALE.fieldOf("scale").forGetter(ScalableParticleOptionsBase::getScale)
            ).apply(instance, TrailParticleOptions::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, TrailParticleOptions> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VECTOR3F, TrailParticleOptions::getColor,
            ByteBufCodecs.FLOAT, ScalableParticleOptionsBase::getScale,
            TrailParticleOptions::new
    );

    private final Vector3f color;

    public TrailParticleOptions(Vector3f color, float scale) {
        super(scale);
        this.color = color;
    }

    public static TrailParticleOptions of(int rgb, float scale) {
        return new TrailParticleOptions(Vec3.fromRGB24(rgb).toVector3f(), scale);
    }

    @Override
    public ParticleType<TrailParticleOptions> getType() {
        return ASParticleRegistry.TRAIL.get();
    }

    public Vector3f getColor() {
        return this.color;
    }
}
