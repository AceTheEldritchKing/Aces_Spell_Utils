package net.acetheeldritchking.aces_spell_utils.items.example.items.misc;

import net.acetheeldritchking.aces_spell_utils.dome.DomeConfig;
import net.acetheeldritchking.aces_spell_utils.utils.DomeHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ExampleDomeItem extends Item {
    public ExampleDomeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level instanceof ServerLevel serverLevel) {
            Vec3 look = player.getLookAngle();
            Vec3 flat = new Vec3(look.x, 0.0, look.z);
            // centred ahead of the player and kept smaller than that offset, so the camera stays outside the shell where the rim is visible
            Vec3 center = player.position().add(flat.lengthSqr() < 1.0E-6 ? new Vec3(0.0, 0.0, 12.0) : flat.normalize().scale(12.0));
            DomeHandler.trigger(serverLevel, center, DomeConfig.of(0xFF2020, 3.0F, 200));
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }
}
