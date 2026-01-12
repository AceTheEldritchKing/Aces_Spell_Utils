package net.acetheeldritchking.aces_spell_utils.spells;

import io.redspace.ironsspellbooks.api.events.SpellSummonEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import io.redspace.ironsspellbooks.capabilities.magic.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public abstract class AbstractSummonSpell extends AbstractSpell {
    @Override
    public ICastDataSerializable getEmptyCastData() {
        return new SummonedEntitiesCastData();
    }

    @Override
    public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
        return 2;
    }

    @Override
    public void onRecastFinished(ServerPlayer serverPlayer, RecastInstance recastInstance, RecastResult recastResult, ICastDataSerializable castDataSerializable) {
        if (SummonManager.recastFinishedHelper(serverPlayer, recastInstance, recastResult, castDataSerializable)) {
            super.onRecastFinished(serverPlayer, recastInstance, recastResult, castDataSerializable);
        }
    }

    /**
     * Called when summon should occur
     *
     * @return How long the summoned entity should last
     */
    protected abstract int onSummoningCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData, SummonedEntitiesCastData castData);

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        PlayerRecasts recasts = playerMagicData.getPlayerRecasts();
        if (!recasts.hasRecastForSpell(this)) {
            var castData = new SummonedEntitiesCastData();
            var summonTimer = onSummoningCast(level, spellLevel, entity, castSource, playerMagicData, castData);

            RecastInstance recastInstance = new RecastInstance(this.getSpellId(), spellLevel, getRecastCount(spellLevel, entity), summonTimer, castSource, castData);
            recasts.addRecast(recastInstance, playerMagicData);
        }
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    protected <K extends Mob> void spawnHelper(double x, double y, double z, LivingEntity caster, Level level, int summonTimer, SummonedEntitiesCastData castData, Supplier<K> entitySupplier, ResourceLocation spellId, int spellLevel) {
        K entity = entitySupplier.get();

        entity.moveTo(x, y, z);

        var event = NeoForge.EVENT_BUS.post(new SpellSummonEvent<>(caster, entity, spellId, spellLevel)).getCreature();

        level.addFreshEntity(event);
        SummonManager.initSummon(caster, event, summonTimer, castData);
    }
}
