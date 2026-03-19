package net.acetheeldritchking.aces_spell_utils.registries;

import net.acetheeldritchking.aces_spell_utils.AcesSpellUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class ASDamageTypes {
    public static ResourceKey<DamageType> register(String name)
    {
        return ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse(ResourceLocation.fromNamespaceAndPath(AcesSpellUtils.MOD_ID, name).toString()));
    }

    // Ritual
    public static final ResourceKey<DamageType> RITUAL_MAGIC = register("ritual_magic");
    public static final ResourceKey<DamageType> HYDRO_MAGIC = register("hydro_magic");
    public static final ResourceKey<DamageType> TECHNOMANCY_MAGIC = register("technomancy_magic");
}
