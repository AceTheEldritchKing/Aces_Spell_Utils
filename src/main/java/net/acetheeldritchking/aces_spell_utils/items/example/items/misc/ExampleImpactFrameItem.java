package net.acetheeldritchking.aces_spell_utils.items.example.items.misc;

import net.acetheeldritchking.aces_spell_utils.utils.ImpactFrameHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ExampleImpactFrameItem extends Item {
    private final int brightColor;

    public ExampleImpactFrameItem(Properties properties, int brightColor) {
        super(properties);
        this.brightColor = brightColor;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            ImpactFrameHandler.trigger(serverPlayer, brightColor, 0.85f, 20);
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }
}
