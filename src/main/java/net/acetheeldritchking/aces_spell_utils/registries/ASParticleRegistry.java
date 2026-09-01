package net.acetheeldritchking.aces_spell_utils.registries;

import com.mojang.serialization.MapCodec;
import net.acetheeldritchking.aces_spell_utils.AcesSpellUtils;
import net.acetheeldritchking.aces_spell_utils.particles.TrailParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ASParticleRegistry {
    private static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, AcesSpellUtils.MOD_ID);

    public static final DeferredHolder<ParticleType<?>, ParticleType<TrailParticleOptions>> TRAIL =
            PARTICLE_TYPES.register("trail", () -> new ParticleType<TrailParticleOptions>(false) {
                @Override
                public MapCodec<TrailParticleOptions> codec() {
                    return TrailParticleOptions.CODEC;
                }

                @Override
                public StreamCodec<? super RegistryFriendlyByteBuf, TrailParticleOptions> streamCodec() {
                    return TrailParticleOptions.STREAM_CODEC;
                }
            });

    public static void register(IEventBus eventBus)
    {
        PARTICLE_TYPES.register(eventBus);
    }
}
