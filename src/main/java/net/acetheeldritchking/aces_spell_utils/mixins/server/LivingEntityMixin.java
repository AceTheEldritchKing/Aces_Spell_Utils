package net.acetheeldritchking.aces_spell_utils.mixins.server;

import net.acetheeldritchking.aces_spell_utils.registries.ASAttributeRegistry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Doing what Artifacts does so that it's more foolproof because apparently doing it via event doesn't work
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "hurt", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/LivingEntity;invulnerableTime:I", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER))
    private void hurt(DamageSource source, float f, CallbackInfoReturnable<Boolean> cir)
    {
        asu$applyEvasiveAttributeIframeTicks();
    }

    @Inject(method = "handleDamageEvent", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/LivingEntity;invulnerableTime:I", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER))
    private void handleDamageEvent(DamageSource source, CallbackInfo cir)
    {
        asu$applyEvasiveAttributeIframeTicks();
    }

    @SuppressWarnings("ConstantConditions")
    @Unique
    private void asu$applyEvasiveAttributeIframeTicks()
    {
        LivingEntity entity = (LivingEntity) (Object) this;
        int bonusTicks = (int) entity.getAttributeValue(ASAttributeRegistry.EVASIVE);
        entity.invulnerableTime += bonusTicks;
    }
}
