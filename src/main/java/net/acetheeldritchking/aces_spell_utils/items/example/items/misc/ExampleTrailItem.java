package net.acetheeldritchking.aces_spell_utils.items.example.items.misc;

import net.acetheeldritchking.aces_spell_utils.particles.TrailParticleOptions;
import net.acetheeldritchking.aces_spell_utils.trail.TrailConfig;
import net.acetheeldritchking.aces_spell_utils.utils.TrailHandler;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ExampleTrailItem extends Item {
    public ExampleTrailItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            if (TrailHandler.isAttached(player)) {
                TrailHandler.detach(player);
            } else {
                TrailHandler.attach(player, new TrailConfig(TrailParticleOptions.of(0x39FF6A, 1.0F), 3, 1.0F, 0.05F, 0.0F));
            }
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }
}
