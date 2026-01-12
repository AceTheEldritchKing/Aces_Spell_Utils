package net.acetheeldritchking.aces_spell_utils.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.capabilities.magic.SummonedEntitiesCastData;
import io.redspace.ironsspellbooks.entity.mobs.SummonedSkeleton;
import net.acetheeldritchking.aces_spell_utils.AcesSpellUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * This is not used anywhere, simply serves the purpose of being an example
 */
public class ExampleSummonSpell extends AbstractSummonSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(AcesSpellUtils.MOD_ID, "example_summon_spells");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.LEGENDARY)
            .setSchoolResource(SchoolRegistry.NATURE_RESOURCE)
            .setMaxLevel(1)
            .setCooldownSeconds(200)
            .build();

    public ExampleSummonSpell()
    {
        this.manaCostPerLevel = 20;
        this.baseSpellPower = 10;
        this.spellPowerPerLevel = 5;
        this.castTime = 80;
        this.baseManaCost = 200;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return spellId;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    @Override
    protected int onSummoningCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData, SummonedEntitiesCastData castData) {
        int summonTimer = 20 * 60 * 10;

        for (int i = 0; i < spellLevel; i++) {
            Vec3 vec = caster.getEyePosition();

            double randomNearbyX = vec.x + caster.getRandom().nextGaussian() * 3;
            double randomNearbyZ = vec.z + caster.getRandom().nextGaussian() * 3;

            spawnHelper(randomNearbyX, vec.y, randomNearbyZ, caster, level, summonTimer, castData, () -> new SummonedSkeleton(level, caster, true), spellId, spellLevel);
        }

        return summonTimer;
    }
}
