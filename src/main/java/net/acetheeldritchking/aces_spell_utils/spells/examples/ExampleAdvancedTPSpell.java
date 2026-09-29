package net.acetheeldritchking.aces_spell_utils.spells.examples;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.spells.ender.TeleportSpell;
import net.acetheeldritchking.aces_spell_utils.AcesSpellUtils;
import net.acetheeldritchking.aces_spell_utils.spells.ASSpellAnimations;
import net.acetheeldritchking.aces_spell_utils.spells.AdvancedTeleportSpell;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ExampleAdvancedTPSpell extends AdvancedTeleportSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(AcesSpellUtils.MOD_ID, "example_advanced_tp");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.LEGENDARY)
            .setSchoolResource(SchoolRegistry.EVOCATION_RESOURCE)
            .setMaxLevel(1)
            .setCooldownSeconds(15)
            .build();

    public ExampleAdvancedTPSpell()
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
    public List<AnimationHolder> startAnimations() {
        return List.of(
                ASSpellAnimations.ANIMATION_RIGHT_HORIZONTAL_SWORD_SLASH,
                ASSpellAnimations.ANIMATION_LEFT_HORIZONTAL_SWORD_SLASH,
                ASSpellAnimations.ANIMATION_GROUND_FIST_SLAM
        );
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        Vec3 dest = null;
        var tpData = (AdvancedTeleportSpell.AdvancedTeleportData) playerMagicData.getAdditionalCastData();

        if (tpData != null)
        {
            var potentialTarget = tpData.getTeleportTargetPos();
            if (potentialTarget != null)
            {
                dest = potentialTarget;
            }
        }

        if (dest == null)
        {
            dest = findDirectionalTeleportLocation(level, entity, 5);
        }

        Utils.handleSpellTeleport(this, entity, dest);
        entity.resetFallDistance();

        playerMagicData.resetAdditionalCastData();

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }
}
