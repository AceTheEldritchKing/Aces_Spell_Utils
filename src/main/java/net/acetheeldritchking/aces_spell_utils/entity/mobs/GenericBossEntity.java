package net.acetheeldritchking.aces_spell_utils.entity.mobs;

import io.redspace.ironsspellbooks.api.network.IClientEventEntity;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;

public abstract class GenericBossEntity extends AbstractSpellCastingMob implements Enemy, IClientEventEntity {
    // This class is a generic and abstract class used for bosses
    // In here are helpful methods for handling phases and boss music
    public GenericBossEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    // Phase Serializer
    public final static EntityDataAccessor<Integer> PHASE = SynchedEntityData.defineId(GenericBossEntity.class, EntityDataSerializers.INT);

    // Used for boss music; set it in the child class to the music you want to have play
    public SoundEvent getBossMusic() {

        return null;
    }

    @Override
    public void handleClientEvent(byte b) {
        // Music will be handled here, will be overridden by child classes
    }

    // Phase stuff //
    public enum Phase
    {
        FirstPhase(0),
        SecondPhase(1),
        ThirdPhase(2),
        FourthPhase(3),
        FifthPhase(4),
        SixthPhase(5),
        SeventhPhase(6),
        EighthPhase(7),
        NinethPhase(8),
        TenthPhase(9),
        EleventhPhase(10),
        TwelfthPhase(11);

        final public int value;

        Phase(int value)
        {
            this.value = value;
        }
    }

    public void setPhase(int phase)
    {
        this.entityData.set(PHASE, phase);
    }

    public void setPhase(Phase phase)
    {
        this.setPhase(phase.value);
    }

    public int getPhase()
    {
        return this.entityData.get(PHASE);
    }

    public boolean isPhase(Phase phase)
    {
        return phase.value == getPhase();
    }

    // NBT
    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        // Phases
        setPhase(pCompound.getInt("phase"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        // Phases
        pCompound.putInt("phase", getPhase());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(PHASE, 0);
    }
}
