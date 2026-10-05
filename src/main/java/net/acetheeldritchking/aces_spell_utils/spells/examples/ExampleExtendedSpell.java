package net.acetheeldritchking.aces_spell_utils.spells.examples;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import net.acetheeldritchking.aces_spell_utils.AcesSpellUtils;
import net.acetheeldritchking.aces_spell_utils.spells.ASSpellAnimations;
import net.acetheeldritchking.aces_spell_utils.spells.ExtendedAbstractSpell;
import net.acetheeldritchking.aces_spell_utils.utils.ASUtils;
import net.acetheeldritchking.aces_spell_utils.utils.AcesSpellUtilsConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

public class ExampleExtendedSpell extends ExtendedAbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(AcesSpellUtils.MOD_ID, "example_advanced_spells");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.LEGENDARY)
            .setSchoolResource(SchoolRegistry.EVOCATION_RESOURCE)
            .setMaxLevel(1)
            .setCooldownSeconds(15)
            .setDeprecated(true)
            .build();

    public ExampleExtendedSpell()
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
        return CastType.INSTANT;
    }

    @Override
    public boolean hasRandomStartAnim() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return AcesSpellUtilsConfig.devMode;
    }

    @Override
    public List<AnimationHolder> startAnimations() {
        return List.of(
                ASSpellAnimations.ANIMATION_LEFT_HANDED_PUNCH,
                ASSpellAnimations.ANIMATION_RIGHT_HANDED_PUNCH
        );
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        ASUtils.spawnParticlesInCircle(8, 1.5F, 0.5F, 0.1F, entity, ParticleTypes.SCULK_CHARGE_POP);

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }
}
