package net.acetheeldritchking.aces_spell_utils.items.example.items.misc;

import net.acetheeldritchking.aces_spell_utils.ribbon.ColorRamp;
import net.acetheeldritchking.aces_spell_utils.ribbon.Curve;
import net.acetheeldritchking.aces_spell_utils.ribbon.Easing;
import net.acetheeldritchking.aces_spell_utils.ribbon.RibbonConfig;
import net.acetheeldritchking.aces_spell_utils.utils.RibbonHandler;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ExampleRibbonItem extends Item {
    public ExampleRibbonItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            if (RibbonHandler.isAttached(player)) {
                RibbonHandler.detach(player);
            } else {
                RibbonHandler.attach(player, new RibbonConfig(
                        new ColorRamp(new int[]{0x2E1A66, 0x66E0FF, 0xFFFFFF}, Easing.LINEAR),
                        new Curve(0.18F, 0.0F, Easing.EASE_OUT_QUAD),
                        new Curve(1.0F, 0.0F, Easing.LINEAR),
                        24,
                        true));
            }
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }
}
