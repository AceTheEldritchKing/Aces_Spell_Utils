package net.acetheeldritchking.aces_spell_utils.entity.spells;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractDomainEntity extends AbstractMagicProjectile {
    private int radius;
    private int refinement;
    private boolean open;
    private ArrayList<AbstractDomainEntity> clashingWith;
    private boolean hasTransported;
    private boolean finishedSpawnAnim;

    public AbstractDomainEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setNoGravity(true);
        hasTransported = false;
    }

    public void onActivation(){
        level().getEntitiesOfClass(AbstractDomainEntity.class, new AABB(this.position().subtract(radius / 2.0, radius / 2.0, radius / 2.0), this.position().add(radius / 2.0, radius / 2.0, radius / 2.0))).stream()
                .forEach(e -> {
                            if(e.distanceTo(this) < radius && !Objects.equals(e,this)){
                                if(e.getRefinement() > this.refinement){
                                    this.destroyDomain();
                                }else if (e.getRefinement() < this.refinement){
                                    e.destroyDomain();
                                }else{
                                    if(!clashingWith.contains(e)) {
                                        clashingWith.add(e);
                                    }
                                    if(!e.getClashingWith().contains(this)) {
                                        e.getClashingWith().add(this);
                                    }
                                }
                            }
                        }
                );
    }

    //TODO: Is there a nice way to get this method to be used instead of discard() so that people can have breaking domain animations?
    public void destroyDomain(){
        this.discard();
    }

    private boolean canTransport(){
        return !open && !hasTransported && this.clashingWith.isEmpty() && finishedSpawnAnim;
    }

    public void handleTransportation(){
        hasTransported = true;
    }

    public void handleDomainClash(AbstractDomainEntity opposingDomain){

    }

    public void targetSureHit(){
        level().getEntitiesOfClass(Entity.class, new AABB(this.position().subtract(radius / 2.0, radius / 2.0, radius / 2.0), this.position().add(radius / 2.0, radius / 2.0, radius / 2.0))).stream()
                .forEach(e -> {
                            if(e.distanceTo(this) < radius && canTarget(e)){
                                handleSureHit(e);
                            }
                        }
                );
    }

    public void handleSureHit(Entity e){

    }

    public boolean isClashing(){
        return !clashingWith.isEmpty();
    }

    public boolean canTarget(Entity e){
        boolean shareOwner = false;
        if(e instanceof TamableAnimal tame){
            shareOwner = Objects.equals(tame.getOwner(), ((TamableAnimal) e).getOwner());
        }
        if(e instanceof Projectile proj){
            shareOwner = Objects.equals(proj.getOwner(), e);
        }
        return !(Objects.equals(e, this) || Objects.equals(e,this.getOwner()) || shareOwner);
    }

    public int getRefinement() {
        return refinement;
    }

    public void setRefinement(int refinement) {
        this.refinement = refinement;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public ArrayList<AbstractDomainEntity> getClashingWith() {
        return clashingWith;
    }

    @Override
    public void tick() {
        if(this.tickCount == 0) {
            onActivation();
        }
        if(this.getOwner() instanceof LivingEntity living && living.isDeadOrDying()){
            destroyDomain();
        }
        for(AbstractDomainEntity e : clashingWith){
            if(e != null) {
                handleDomainClash(e);
            }else{
                clashingWith.remove(e);
            }
        }
        if(canTransport()){
            handleTransportation();
        }
        targetSureHit();
        super.tick();
    }

    @Override
    public void trailParticles() {
    }

    @Override
    public void impactParticles(double x, double y, double z) {
    }

    @Override
    public float getSpeed() {
        return 0;
    }

    @Override
    protected void rotateWithMotion() {
    }

    @Override
    public Optional<Holder<SoundEvent>> getImpactSound() {
        return Optional.empty();
    }

    @Override
    public void onAntiMagic(MagicData playerMagicData) {
    }
}
