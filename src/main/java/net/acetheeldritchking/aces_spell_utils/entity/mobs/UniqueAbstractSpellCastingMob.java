package net.acetheeldritchking.aces_spell_utils.entity.mobs;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.magic.SpellSelectionManager;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.spells.fire.BurningDashSpell;
import net.acetheeldritchking.aces_spell_utils.utils.ASUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

// Used for making custom mob models and allow them to have custom spell casting animations
// TO DO: Make tags for stomp spells and slash spells to have their own casting animation
public abstract class UniqueAbstractSpellCastingMob extends AbstractSpellCastingMob implements GeoEntity, IMagicEntity {
    private static final EntityDataAccessor<Boolean> DATA_CANCEL_CAST = SynchedEntityData.defineId(UniqueAbstractSpellCastingMob.class, EntityDataSerializers.BOOLEAN);
    private SpellData castingSpell;
    protected AbstractSpell lastCastSpellType = SpellRegistry.none();
    protected AbstractSpell instantCastSpellType = SpellRegistry.none();
    protected boolean cancelCastAnimation = false;
    protected boolean animatingLegs = false;
    private final MagicData playerMagicData = new MagicData(true);
    private boolean recreateSpell;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public UniqueAbstractSpellCastingMob(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.playerMagicData.setSyncedData(new SyncedSpellData(this));
        this.noCulling = true;
        this.lookControl = this.createLookControl();
    }

    protected LookControl createLookControl()
    {
        return new LookControl(this)
        {
            @Override
            protected float rotateTowards(float from, float to, float maxDelta) {
                return super.rotateTowards(from, to, maxDelta * 2.5F);
            }

            @Override
            protected boolean resetXRotOnTick() {
                return getTarget() == null;
            }
        };
    }

    public MagicData getMagicData() {
        return this.playerMagicData;
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        super.onSyncedDataUpdated(pKey);
        if (this.level().isClientSide) {
            if (pKey.id() == DATA_CANCEL_CAST.id()) {
                IronsSpellbooks.LOGGER.debug("ASCM.onSyncedDataUpdated.1 this.isCasting:{}, playerMagicData.isCasting:{} isClient:{}", new Object[]{this.isCasting(), this.playerMagicData == null ? "null" : this.playerMagicData.isCasting(), this.level().isClientSide()});
                this.cancelCast();
            }

        }
    }

    public void cancelCast() {
        if (this.isCasting()) {
            if (this.level().isClientSide) {
                this.cancelCastAnimation = true;
            } else {
                this.entityData.set(DATA_CANCEL_CAST, !(Boolean)this.entityData.get(DATA_CANCEL_CAST));
            }

            this.castComplete();
        }

    }

    public void castComplete() {
        if (!this.level().isClientSide) {
            if (this.castingSpell != null) {
                this.castingSpell.getSpell().onServerCastComplete(this.level(), this.castingSpell.getLevel(), this, this.playerMagicData, false);
            }
        } else {
            this.playerMagicData.resetCastingState();
        }

        this.castingSpell = null;
    }

    public void setSyncedSpellData(SyncedSpellData syncedSpellData) {
        if (this.level().isClientSide) {
            boolean isCasting = this.playerMagicData.isCasting();
            this.playerMagicData.setSyncedData(syncedSpellData);
            this.castingSpell = this.playerMagicData.getCastingSpell();
            IronsSpellbooks.LOGGER.debug("ASCM.setSyncedSpellData playerMagicData:{}, priorIsCastingState:{}, spell:{}", new Object[]{this.playerMagicData, isCasting, this.castingSpell});
            if (this.castingSpell != null) {
                if (!this.playerMagicData.isCasting() && isCasting) {
                    this.castComplete();
                } else if (this.playerMagicData.isCasting() && !isCasting) {
                    AbstractSpell spell = this.playerMagicData.getCastingSpell().getSpell();
                    this.initiateCastSpell(spell, this.playerMagicData.getCastingSpellLevel());
                    if (this.castingSpell.getSpell().getCastType() == CastType.INSTANT) {
                        this.instantCastSpellType = this.castingSpell.getSpell();
                        this.castingSpell.getSpell().onClientPreCast(this.level(), this.castingSpell.getLevel(), this, InteractionHand.MAIN_HAND, this.playerMagicData);
                        this.castComplete();
                    }
                }

            }
        }
    }

    protected void customServerAiStep() {
        super.customServerAiStep();
        if (this.recreateSpell) {
            this.recreateSpell = false;
            SyncedSpellData syncedSpellData = this.playerMagicData.getSyncedData();
            AbstractSpell spell = SpellRegistry.getSpell(syncedSpellData.getCastingSpellId());
            this.initiateCastSpell(spell, syncedSpellData.getCastingSpellLevel());
        }

        if (this.castingSpell != null) {
            this.playerMagicData.handleCastDuration();
            if (this.playerMagicData.isCasting()) {
                this.castingSpell.getSpell().onServerCastTick(this.level(), this.castingSpell.getLevel(), this, this.playerMagicData);
            }

            IronsSpellbooks.LOGGER.debug("ASCM.customServerAiStep.1");
            this.forceLookAtTarget(this.getTarget());
            if (this.playerMagicData.getCastDurationRemaining() <= 0) {
                IronsSpellbooks.LOGGER.debug("ASCM.customServerAiStep.2");
                if (this.castingSpell.getSpell().getCastType() == CastType.LONG || this.castingSpell.getSpell().getCastType() == CastType.INSTANT) {
                    IronsSpellbooks.LOGGER.debug("ASCM.customServerAiStep.3");
                    this.castingSpell.getSpell().onCast(this.level(), this.castingSpell.getLevel(), this, CastSource.MOB, this.playerMagicData);
                }

                this.castComplete();
            } else if (this.castingSpell.getSpell().getCastType() == CastType.CONTINUOUS && (this.playerMagicData.getCastDurationRemaining() + 1) % 10 == 0) {
                this.castingSpell.getSpell().onCast(this.level(), this.castingSpell.getLevel(), this, CastSource.MOB, this.playerMagicData);
            }

        }
    }

    public void initiateCastSpell(AbstractSpell spell, int spellLevel) {
        IronsSpellbooks.LOGGER.debug("ASCM.initiateCastSpell: spellType:{} spellLevel:{}, isClient:{}", new Object[]{spell.getSpellId(), spellLevel, this.level().isClientSide});
        if (spell == SpellRegistry.none()) {
            this.castingSpell = null;
        } else {
            if (this.level().isClientSide) {
                this.cancelCastAnimation = false;
            }

            this.castingSpell = new SpellData(spell, spellLevel);
            if (this.getTarget() != null) {
                this.forceLookAtTarget(this.getTarget());
            }

            if (!this.level().isClientSide && !this.castingSpell.getSpell().checkPreCastConditions(this.level(), spellLevel, this, this.playerMagicData)) {
                IronsSpellbooks.LOGGER.debug("ASCM.precastfailed: spellType:{} spellLevel:{}, isClient:{}", new Object[]{spell.getSpellId(), spellLevel, this.level().isClientSide});
                this.castingSpell = null;
            } else {
                if (spell != SpellRegistry.TELEPORT_SPELL.get() && spell != SpellRegistry.FROST_STEP_SPELL.get()) {
                    if (spell == SpellRegistry.BLOOD_STEP_SPELL.get()) {
                        this.setTeleportLocationBehindTarget(3);
                    } else if (spell == SpellRegistry.BURNING_DASH_SPELL.get()) {
                        this.setBurningDashDirectionData();
                    }
                } else {
                    this.setTeleportLocationBehindTarget(10);
                }

                this.playerMagicData.initiateCast(this.castingSpell.getSpell(), this.castingSpell.getLevel(), this.castingSpell.getSpell().getEffectiveCastTime(this.castingSpell.getLevel(), this), CastSource.MOB, SpellSelectionManager.MAINHAND);
                if (!this.level().isClientSide) {
                    this.castingSpell.getSpell().onServerPreCast(this.level(), this.castingSpell.getLevel(), this, this.playerMagicData);
                }

            }
        }
    }

    public boolean isCasting() {
        return this.playerMagicData.isCasting();
    }

    public void setBurningDashDirectionData() {
        this.playerMagicData.setAdditionalCastData(new BurningDashSpell.BurningDashDirectionOverrideCastData());
    }

    private void forceLookAtTarget(LivingEntity target) {
        if (target != null) {
            double d0 = target.getX() - this.getX();
            double d2 = target.getZ() - this.getZ();
            double d1 = target.getEyeY() - this.getEyeY();

            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            float f = (float) (Mth.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
            float f1 = (float) (-(Mth.atan2(d1, d3) * (double) (180F / (float) Math.PI)));
            this.setXRot(f1 % 360);
            this.setYRot(f % 360);
        }
    }

    // Geckolib & Animations
    // I'm tired, forgive me for copying code </3
    private final AnimationController animationController = new AnimationController<>(this, "controller", 0, this::predicate);
    private final AnimationController instantCastAnimationController = new AnimationController<>(this, "instant_cast_controller", 0, this::instantCastPredicate);
    private final AnimationController longCastAnimationController = new AnimationController<>(this, "long_cast_controller", 0, this::longCastPredicate);
    private final AnimationController contCastAnimationController = new AnimationController<>(this, "continuous_cast_controller", 0, this::continuousCastPredicate);
    private final AnimationController castingAnimationController = new AnimationController<>(this, "casting_controller", 0, this::castingPredicate);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(animationController);
        controllers.add(instantCastAnimationController);
        controllers.add(longCastAnimationController);
        controllers.add(contCastAnimationController);
    }

    private PlayState predicate(AnimationState event)
    {
        if (event.isMoving())
        {
            //System.out.println("Set is moving");
            event.getController().setAnimation(RawAnimation.begin().then("walking", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        else if (!event.isMoving())
        {
            //System.out.println("Set is idle");
            event.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        return PlayState.STOP;
    }

    private PlayState instantCastPredicate(AnimationState event)
    {
        //System.out.println("Instant predicate");
        if (cancelCastAnimation)
        {
            return PlayState.STOP;
        }

        var controller = event.getController();
        if (instantCastSpellType != SpellRegistry.none() && controller.getAnimationState() == AnimationController.State.STOPPED)
        {
            setStartAnimationFromSpell(controller, instantCastSpellType);
            instantCastSpellType = SpellRegistry.none();
        }
        return PlayState.CONTINUE;
    }

    private PlayState longCastPredicate(AnimationState event)
    {
        //System.out.println("Long predicate");
        var controller = event.getController();

        if (cancelCastAnimation || (controller.getAnimationState() == AnimationController.State.STOPPED && !(isCasting() && castingSpell != null && castingSpell.getSpell().getCastType() == CastType.LONG)))
        {
            return PlayState.STOP;
        }

        if (isCasting() && this.castingSpell != null)
        {
            //System.out.println("Is casting?");
            if (controller.getAnimationState() == AnimationController.State.STOPPED)
            {
                //System.out.println("Set long cast animation");
                setStartAnimationFromSpell(controller, castingSpell.getSpell());
            }
        }

        return PlayState.CONTINUE;
    }

    private PlayState continuousCastPredicate(AnimationState event)
    {
        //System.out.println("Continuous predicate");
        var controller = event.getController();

        if (cancelCastAnimation || (controller.getAnimationState() == AnimationController.State.STOPPED && !(isCasting() && castingSpell != null && castingSpell.getSpell().getCastType() == CastType.LONG)))
        {
            return PlayState.STOP;
        }

        if (isCasting() && this.castingSpell != null)
        {
            //System.out.println("Is casting?");
            if (controller.getAnimationState() == AnimationController.State.STOPPED)
            {
                //System.out.println("Set continuous cast animation");
                setStartAnimationFromSpell(controller, castingSpell.getSpell());
            }
        }

        return PlayState.CONTINUE;
    }

    // For testing purposes
    private PlayState castingPredicate(AnimationState event)
    {
        var controller = event.getController();

        if (isCasting())
        {
            controller.forceAnimationReset();
            controller.setAnimation(RawAnimation.begin().thenPlay("long_cast"));
            return PlayState.CONTINUE;
        }

        return PlayState.STOP;
    }

    protected void setStartAnimationFromSpell(AnimationController controller, AbstractSpell spell) {
        spell.getCastStartAnimation().getForMob().ifPresentOrElse(animationBuilder -> {
            controller.forceAnimationReset();
            if(ASUtils.isLongAnimCast(spell)) {
                //System.out.println("Set Start long cast");
                controller.setAnimation(RawAnimation.begin().then("long_cast", Animation.LoopType.PLAY_ONCE));
            }
            else if (ASUtils.isContAnimCast(spell)) {
                //System.out.println("Set Start cont. cast");
                controller.setAnimation(RawAnimation.begin().then("continous_cast", Animation.LoopType.PLAY_ONCE));
            }
            else {
                //System.out.println("Set Start instant cast");
                controller.setAnimation(RawAnimation.begin().then("instant_cast", Animation.LoopType.PLAY_ONCE));
            }
            lastCastSpellType = spell;
            cancelCastAnimation = false;
            animatingLegs = false;
        }, () -> {
            cancelCastAnimation = true;
        });
    }

    @Override
    public boolean isAnimating() {
        return isCasting()
                || (longCastAnimationController.getAnimationState() != AnimationController.State.STOPPED)
                || (instantCastAnimationController.getAnimationState() != AnimationController.State.STOPPED)
                || (contCastAnimationController.getAnimationState() != AnimationController.State.STOPPED);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    // NBT
    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        SyncedSpellData syncedSpellData = new SyncedSpellData(this);
        syncedSpellData.loadNBTData(pCompound, this.level().registryAccess());
        if (syncedSpellData.isCasting()) {
            this.recreateSpell = true;
        }

        this.playerMagicData.setSyncedData(syncedSpellData);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        this.playerMagicData.getSyncedData().saveNBTData(pCompound, this.level().registryAccess());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_CANCEL_CAST, false);
    }
}