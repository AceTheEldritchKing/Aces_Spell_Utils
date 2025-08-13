package net.acetheeldritchking.aces_spell_utils.entity.mobs;

import io.redspace.ironsspellbooks.api.network.IClientEventEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;

public abstract class GenericUniqueBossEntity extends UniqueAbstractSpellCastingMob implements Enemy, IClientEventEntity {
    // This class is a generic and abstract class used for bosses
    // This one is exclusively for Unique Abstract Spell Casting mobs in case you want a custom modeled boss
    // In here are helpful methods for handling phases and boss music
    public GenericUniqueBossEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    // Phase Serializer
    public final static EntityDataAccessor<Integer> PHASE = SynchedEntityData.defineId(GenericUniqueBossEntity.class, EntityDataSerializers.INT);

    // Used for boss music; set it in the child class to the music you want to have play
    public SoundEvent getBossMusic() {
        return null;
    }

    // This is for changing music based on phase changing
    // As there is no dedicated transition phase, second phase can be
    // Set this to true if you want the music to change
    public boolean changeMusicOnPhaseChange;
    public boolean useSecondPhaseAsTransition;

    // Setter for above values
    public void setChangeMusicOnPhaseChange(boolean val)
    {
        changeMusicOnPhaseChange = val;
    }

    public void setUseSecondPhaseAsTransition(boolean val)
    {
        useSecondPhaseAsTransition = val;
    }

    // Getter for above values
    public boolean getChangeMusicOnPhaseChange()
    {
        return changeMusicOnPhaseChange;
    }

    public boolean getUseSecondPhaseAsTransition()
    {
        return useSecondPhaseAsTransition;
    }

    // Used for transition music
    public SoundEvent getTransitionMusic()
    {
        return null;
    }

    // Used for music to get for other phases
    public SoundEvent getOtherPhaseMusic()
    {
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

    public void setPhase(GenericBossEntity.Phase phase)
    {
        this.setPhase(phase.value);
    }

    public int getPhase()
    {
        return this.entityData.get(PHASE);
    }

    public boolean isPhase(GenericBossEntity.Phase phase)
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
