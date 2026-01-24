package net.acetheeldritchking.aces_spell_utils.entity.spells;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import net.acetheeldritchking.aces_spell_utils.utils.ASUtils;
import net.acetheeldritchking.aces_spell_utils.utils.AcesSpellUtilsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.*;

public abstract class AbstractDomainEntity extends Entity implements AntiMagicSusceptible, INBTSerializable<CompoundTag> {
    //ALL OF THESE SHOULD ONLY BE INTERACTED WITH USING THE GETTERS/SETTERS
    private static final EntityDataAccessor<Integer> RADIUS = SynchedEntityData.defineId(AbstractDomainEntity.class, EntityDataSerializers.INT); //How far does it reach?
    private static final EntityDataAccessor<Integer> REFINEMENT = SynchedEntityData.defineId(AbstractDomainEntity.class, EntityDataSerializers.INT); //How good is it in clashes?
    private static final EntityDataAccessor<Boolean> OPEN  = SynchedEntityData.defineId(AbstractDomainEntity.class, EntityDataSerializers.BOOLEAN); //Will it impose itself into the world (open) or make use of a separate dimension (closed)?
    private static final EntityDataAccessor<Boolean> TRANSPORTED = SynchedEntityData.defineId(AbstractDomainEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> CLASHABLE  = SynchedEntityData.defineId(AbstractDomainEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> CLASHING = SynchedEntityData.defineId(AbstractDomainEntity.class,EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Long> SPAWN_TIME = SynchedEntityData.defineId(AbstractDomainEntity.class, EntityDataSerializers.LONG);
    private static final EntityDataAccessor<Integer> TIME_SPENT_CLASHING = SynchedEntityData.defineId(AbstractDomainEntity.class, EntityDataSerializers.INT);
    private static final Map<AbstractDomainEntity,ArrayList<AbstractDomainEntity>> clashingWithMap = new HashMap<>();
    private static final Map<AbstractDomainEntity, Entity> ownerMap = new HashMap<>();
    private int spawnAnimTime = Integer.MAX_VALUE; //please update this in your own code with setSpawnAnimTime()

    public AbstractDomainEntity(EntityType<? extends Entity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        addClashingMapIfNecessary();
        this.setNoGravity(true);
        this.canUsePortal(false);
    }

    public void onActivation(){
        if(!level().isClientSide) {
            //tickCount gets weird with cross dimensional travel and chunkloading so this is easier
            setSpawnTime(level().getGameTime());
        }
        //forceload the chunk
        ServerChunkCache cache = getServer().getLevel(this.level().dimension()).getChunkSource();
        cache.addRegionTicket(TicketType.FORCED, ASUtils.getChunkPos(new BlockPos((int) this.position().x, (int) this.position().y, (int) this.position().z)), 20, ASUtils.getChunkPos(new BlockPos((int) this.position().x, (int) this.position().y, (int) this.position().z)), true);
        //I know this method shows up a lot but its necessary i promise
        addClashingMapIfNecessary();
        level().getEntitiesOfClass(AbstractDomainEntity.class, new AABB(position().subtract(getRadius() / 2.0, getRadius() / 2.0, getRadius() / 2.0), this.position().add(getRadius() / 2.0, getRadius() / 2.0, getRadius() / 2.0))).stream()
                .forEach(e -> {
                            if(e.distanceTo(this) < getRadius() && !Objects.equals(e,this)){
                                //clash checks!
                                if(!e.getClashable()){
                                    //do nothing if the other domain isn't in a clashable state
                                }else if(e.getOwner() != null && getOwner() != null && e.getOwner().equals(getOwner())){
                                    //System.out.println("SAME OWNER - NO CLASH");
                                    //this would just be really messy
                                }else if((double) e.getRefinement() / getRefinement() >= AcesSpellUtilsConfig.refinementDifference){
                                    //System.out.println("REFINEMENT DIFFERENCE TOO GREAT - NO CLASH");
                                    destroyDomain();
                                }else if ((double) getRefinement() / e.getRefinement() >= AcesSpellUtilsConfig.refinementDifference){
                                    //System.out.println("REFINEMENT DIFFERENCE TOO GREAT - NO CLASH");
                                    e.destroyDomain();
                                }else{
                                    //System.out.println("DOMAIN CLASH DETECTED - Our Domain has Refinement of " + getRefinement());
                                    if(getClashingWith() != null && e.getClashingWith() != null) {
                                        if (!getClashingWith().contains(e)) {
                                            clashingWithMap.get(this).add(e);
                                        }
                                        if (!e.getClashingWith().contains(this)) {
                                            e.getClashingWith().add(this);
                                        }
                                        setClashing(true);
                                        e.setClashing(true);
                                    }
                                }
                            }
                        }
                );
    }

    //Is there a nice way to get this method to be used instead of discard()?
    public void destroyDomain(){
        discard();
        //this is just useful to avoid clogging stuff up but it shouldn't crash the game if not respected
        if(clashingWithMap.get(this) != null) {
            for (AbstractDomainEntity e : clashingWithMap.get(this)) {
                if (e.getClashingWith().contains(this)) {
                    e.getClashingWith().remove(this);
                }
            }
        }
        clashingWithMap.remove(this);
    }

    private boolean canTransport(){
        //I have this as its own method to make it really easy to override, though this case should be perfectly fine
        return !isOpen() && !getTransported() && !isClashing() && tickCount > this.getSpawnAnimTime() && !this.isRemoved();
    }

    //how a domain clash works is entirely up to the addon devs
    public void handleDomainClash(ArrayList<AbstractDomainEntity> opposingDomains){
    }

    //This method needs to be overridden for closed domains, but should work fine for open ones
    public void targetSureHit(){
        level().getEntitiesOfClass(Entity.class, new AABB(position().subtract(getRadius() / 2.0, getRadius() / 2.0, getRadius() / 2.0), position().add(getRadius() / 2.0, getRadius() / 2.0, getRadius() / 2.0))).stream()
                .forEach(e -> {
                            if(e.distanceTo(this) < getRadius() && canTarget(e)){
                                handleSureHit(e);
                            }
                        }
                );
    }

    //Again, the specific sure hit applied by the domain is entirely made by addon devs
    public void handleSureHit(Entity e){

    }

    public void addClashingMapIfNecessary(){
        if(clashingWithMap.get(this) == null){
            clashingWithMap.put(this,new ArrayList<>());
        }
    }

    public boolean canTarget(Entity e){
        boolean shareOwner = false;
        if(e instanceof TamableAnimal tame){
            shareOwner = Objects.equals(tame.getOwner(), ((TamableAnimal) e).getOwner());
        }
        if(e instanceof Projectile proj){
            shareOwner = Objects.equals(proj.getOwner(), e);
        }
        return !(Objects.equals(e, this) || Objects.equals(e,getOwner()) || shareOwner);
    }

    @Override
    public void tick() {
        addClashingMapIfNecessary();
        if(tickCount == 1) {
            //check for clashes immediately on spawning in
            onActivation();
        }
        if(getOwner() instanceof LivingEntity living && living.isDeadOrDying()){
            //if the owner is dead then destroy the domain
            destroyDomain();
        }
        if(getClashingWith().isEmpty() && isClashing()){
            //If you're not actually clashing but you think you are, then update the isClashing() return value
            setClashing(false);
        }
        if(isClashing()) {
            //if you're clashing, then increment the timeSpentClashing variable and perform the domain clashing behavior
            incrementTimeSpentClashing();
            handleDomainClash(clashingWithMap.get(this));
        }
        if(canTransport()){
            //if you can transport, then do so
            handleTransportation();
        }
        if(!isClashing() && (isOpen() || getTransported())) {
            //if you're not clashing and you're either open or you've transported, then perform the sure hit
            targetSureHit();
        }
        super.tick();
    }

    //up to the addon devs to do
    public void handleTransportation() {
        setTransported(true);
    }

    //domains can't be vanishing with a standard counterspell that'd be lame
    @Override
    public void onAntiMagic(MagicData playerMagicData) {
    }

    //GETTERS AND SETTERS FOR ENTITY DATA VALUES

    public ArrayList<AbstractDomainEntity> getClashingWith() {
        if(clashingWithMap.get(this) != null) {
            return clashingWithMap.get(this);
        }else{
            return new ArrayList<>();
        }
    }

    public boolean isClashing(){
        return this.entityData.get(CLASHING);
    }

    public void setClashing(boolean clashing){
        this.entityData.set(CLASHING,clashing);
    }

    public int getRefinement()
    {
        return this.entityData.get(REFINEMENT);
    }

    public void setRefinement(int refinement)
    {
        this.entityData.set(REFINEMENT, refinement);
    }

    public int getRadius()
    {
        return this.entityData.get(RADIUS);
    }

    public void setRadius(int radius)
    {
        this.entityData.set(RADIUS, radius);
    }

    public boolean isOpen()
    {
        return this.entityData.get(OPEN);
    }

    public void setOpen(boolean open)
    {
        this.entityData.set(OPEN, open);
    }

    public boolean getTransported()
    {
        return this.entityData.get(TRANSPORTED);
    }

    public void setTransported(boolean transported)
    {
        this.entityData.set(AbstractDomainEntity.TRANSPORTED, transported);
    }

    public void setOwner(Entity owner){
        ownerMap.put(this,owner);
    }

    public Entity getOwner(){
        return ownerMap.get(this);
    }

    public void setSpawnAnimTime(int spawnAnimTime) {
        this.spawnAnimTime = spawnAnimTime;
    }

    public int getSpawnAnimTime() {
        return spawnAnimTime;
    }

    public Long getSpawnTime() {
        return this.entityData.get(SPAWN_TIME);
    }

    public void setSpawnTime(long spawnTime){
        this.entityData.set(SPAWN_TIME,spawnTime);
    }

    public void setClashable(boolean clashable){
        this.entityData.set(CLASHABLE,clashable);
    }

    public boolean getClashable(){
        return this.entityData.get(CLASHABLE);
    }

    public int getTimeSpentClashing(){
        return this.entityData.get(TIME_SPENT_CLASHING);
    }

    public void setTimeSpentClashing(int timeSpentClashing){
        this.entityData.set(TIME_SPENT_CLASHING,timeSpentClashing);
    }

    public void incrementTimeSpentClashing(){
        this.entityData.set(TIME_SPENT_CLASHING,getTimeSpentClashing() + 1);
    }

    //ENTITY DATA DEFINING

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.setRadius(tag.getInt("Radius"));
        this.setRefinement(tag.getInt("Refinement"));
        this.setOpen(tag.getBoolean("Open"));
        this.setTransported(tag.getBoolean("Transported"));
        this.setSpawnTime(tag.getLong("Spawn Time"));
        this.setClashable(tag.getBoolean("Clashable"));
        this.setTimeSpentClashing(tag.getInt("Time Spent Clashing"));
        this.setClashing(tag.getBoolean("Clashing"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Radius",this.getRadius());
        tag.putInt("Refinement",this.getRefinement());
        tag.putBoolean("Open",this.isOpen());
        tag.putBoolean("Transported",this.getTransported());
        tag.putLong("Spawn Time",this.getSpawnTime());
        tag.putBoolean("Clashable",this.getClashable());
        tag.putInt("Time Spent Clashing", this.getTimeSpentClashing());
        tag.putBoolean("Clashing",this.isClashing());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(RADIUS, 0);
        builder.define(REFINEMENT,0);
        builder.define(OPEN,false);
        builder.define(TRANSPORTED,false);
        builder.define(SPAWN_TIME,Long.MIN_VALUE);
        builder.define(CLASHABLE,true);
        builder.define(CLASHING,false);
        builder.define(TIME_SPENT_CLASHING,0);
    }

    //NBT HANDLING

    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putLong("Spawn Time",getSpawnTime());
        compoundTag.putInt("Time Spent Clashing",getTimeSpentClashing());
        compoundTag.putBoolean("Clashing",isClashing());
        return compoundTag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        setSpawnTime(nbt.getLong("Spawn Time"));
        setTimeSpentClashing(nbt.getInt("Time Spent Clashing"));
        setClashing(nbt.getBoolean("Clashing"));
    }
}