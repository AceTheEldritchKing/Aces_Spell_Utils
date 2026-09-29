package net.acetheeldritchking.aces_spell_utils.spells;

import io.redspace.ironsspellbooks.api.spells.ICastData;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

public abstract class AdvancedTeleportSpell extends ExtendedAbstractSpell {
    // For normal TP behavior
    public static Vec3 solveStandardTeleportDestination(Level level, LivingEntity entity, BlockPos blockPos, Vec3 vec3)
    {
        BlockPos pos = blockPos;
        Vec3 bbOffset = entity.getForward().normalize().multiply(entity.getBbWidth() / 3, 0, entity.getBbHeight() / 3);
        Vec3 bbImpact = vec3.subtract(bbOffset);

        double ledgeY = level.clip(new ClipContext(Vec3.atBottomCenterOf(pos).add(0, 3, 0), Vec3.atBottomCenterOf(pos), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty())).getLocation().y;
        boolean isAir = level.getBlockState(new BlockPos(new Vec3i(pos.getX(), (int) ledgeY, pos.getZ())).above()).isAir();
        boolean los = level.clip(new ClipContext(bbImpact, bbImpact.add(0, ledgeY - pos.getY(), 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity)).getType() == HitResult.Type.MISS;

        if (isAir && los && Math.abs(ledgeY - pos.getY()) <= 3)
        {
            return new Vec3(pos.getX() + .5, ledgeY + 0.001, pos.getZ() + .5);
        }

        return level.clip(new ClipContext(bbImpact, bbImpact.add(0, -entity.getBbHeight(), 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity)).getLocation().add(0, 0.001, 0);
    }

    public static Vec3 findStandardTeleportLocation(Level level, LivingEntity livingEntity, float maxDist)
    {
        var blockHitResult = Utils.getTargetBlock(level, livingEntity, ClipContext.Fluid.NONE, maxDist);

        return solveStandardTeleportDestination(level, livingEntity, blockHitResult.getBlockPos(), blockHitResult.getLocation());
    }

    // Directional TP
    public static Vec3 findDirectionalTeleportDestination(LivingEntity entity, double strength)
    {
        Vec3 movement = entity.getKnownMovement();

        Vec3 velocity = new Vec3(movement.x, 0, movement.z);
        Vec3 direction = velocity.normalize();
        Vec3 destination = entity.position().add(direction.scale(strength));

        return destination;
    }

    // This method grrrr
    public static BlockHitResult getDirectionalTargetBlock(Level level, LivingEntity entity, ClipContext.Fluid clipContext, double reach) {
        /**
         * var rotation = entity.getLookAngle().normalize().scale(reach);
         * var pos = entity.getEyePosition();
         * var dest = rotation.add(pos);
         * return level.clip(new ClipContext(pos, dest, ClipContext.Block.COLLIDER, clipContext, entity));
         */

        Vec3 movement = entity.getKnownMovement();

        Vec3 velocity = new Vec3(movement.x, 0, movement.z);

        var direction = entity.position().normalize().add(velocity.scale(reach));
        var pos = entity.getEyePosition();
        var dest = direction.add(pos);
        return level.clip(new ClipContext(pos, dest, ClipContext.Block.COLLIDER, clipContext, entity));
    }

    public static Vec3 solveDirectionalTeleportDestination(Level level, LivingEntity entity, BlockPos blockPos, Vec3 vec3)
    {
        BlockPos pos = blockPos;
        Vec3 bbOffset = entity.position().normalize().multiply(entity.getBbWidth() / 3, 0, entity.getBbHeight() / 3);
        Vec3 bbImpact = vec3.subtract(bbOffset);

        double ledgeY = level.clip(new ClipContext(Vec3.atBottomCenterOf(pos).add(0, 3, 0), Vec3.atBottomCenterOf(pos), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty())).getLocation().y;
        boolean isAir = level.getBlockState(new BlockPos(new Vec3i(pos.getX(), (int) ledgeY, pos.getZ())).above()).isAir();
        boolean los = level.clip(new ClipContext(bbImpact, bbImpact.add(0, ledgeY - pos.getY(), 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity)).getType() == HitResult.Type.MISS;

        if (isAir && los && Math.abs(ledgeY - pos.getY()) <= 3)
        {
            return new Vec3(pos.getX() + .5, ledgeY + 0.001, pos.getZ() + .5);
        }

        return level.clip(new ClipContext(bbImpact, bbImpact.add(0, -entity.getBbHeight(), 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity)).getLocation().add(0, 0.001, 0);
    }

    public static Vec3 findDirectionalTeleportLocation(Level level, LivingEntity livingEntity, float maxDist)
    {
        var blockHitResult = getDirectionalTargetBlock(level, livingEntity, ClipContext.Fluid.NONE, maxDist);

        return solveDirectionalTeleportDestination(level, livingEntity, blockHitResult.getBlockPos(), blockHitResult.getLocation());
    }

    // Helper method to determine if the player is moving or not
    // Boolean
    public static boolean isActuallyMoving(LivingEntity entity)
    {
        if (entity != null)
        {
            double dx = entity.getX() - entity.xo;
            double dy = entity.getY() - entity.yo;
            double dz = entity.getZ() - entity.zo;

            return dx * dx + dy * dy + dz * dz > 2.25E-4;
        } else
        {
            return false;
        }
    }

    public static class AdvancedTeleportData implements ICastData
    {
        private Vec3 teleportTargetPos;

        public AdvancedTeleportData(Vec3 teleportTargetPos)
        {
            this.teleportTargetPos = teleportTargetPos;
        }

        public void setTeleportTargetPos(Vec3 teleportTargetPos) {
            this.teleportTargetPos = teleportTargetPos;
        }

        public Vec3 getTeleportTargetPos() {
            return this.teleportTargetPos;
        }

        @Override
        public void reset() {
            // Nada aqui
        }
    }
}
