package net.acetheeldritchking.aces_spell_utils.items.example.items.misc;

import net.acetheeldritchking.aces_spell_utils.ribbon.Easing;
import net.acetheeldritchking.aces_spell_utils.roar.RoarConfig;
import net.acetheeldritchking.aces_spell_utils.utils.RoarHandler;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ExampleRoarItem extends Item {
    public ExampleRoarItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            RoarHandler.trigger(player, RoarConfig.zoom(1.0F, 0.5F, 30, Easing.EASE_OUT_QUAD));
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }
}
