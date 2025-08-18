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
    // This is primarily for first phase music
    public SoundEvent getBossMusic() {
        return null;
    }

    // This is for changing music based on phase changing
    // Set hasCustomMusic to be true if you want to use the music manager given by the API
    // As there is no dedicated transition phase, set the transition phase using the following methods
    // Set this to true if you want the music to change
    public static boolean hasCustomMusic;
    public boolean changeMusicOnPhaseChange;
    public boolean hasTransitionPhase;
    public static int usePhaseAsTransition;
    public static int usePhaseForMusicChange;

    // Methods for above values
    public static boolean hasCustomMusic()
    {
        return hasCustomMusic;
    }

    public boolean changeMusicOnPhaseChange()
    {
        return changeMusicOnPhaseChange;
    }

    public boolean hasTransitionPhase()
    {
        return hasTransitionPhase;
    }

    // Input which phase you want to have transition music
    // Put in an integer between 1-11 to denote the phase, this lines up with the enum values for the phases
    public static int usePhaseAsTransition()
    {
        return usePhaseAsTransition;
    }

    // Input which phase you want to have alt music
    // Put in an integer between 1-11 to denote the phase, this lines up with the enum values for the phases
    public static int usePhaseForMusicChange()
    {
        return usePhaseForMusicChange;
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
        TwelfthPhase(11),
        TransitionPhase(12);

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
