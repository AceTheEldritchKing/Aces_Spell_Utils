package net.acetheeldritchking.aces_spell_utils.items.custom;

import net.acetheeldritchking.aces_spell_utils.utils.ASUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.List;

public class LootBagItem extends Item {
    ResourceLocation lootTable;
    ParticleOptions particleTypes;

    public LootBagItem(Properties properties, ResourceLocation lootTable) {
        super(properties);
        this.lootTable = lootTable;
    }

    public LootBagItem(Properties properties, ResourceLocation lootTable, ParticleOptions particleTypes) {
        super(properties);
        this.lootTable = lootTable;
        this.particleTypes = particleTypes;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        tooltipComponents.add(Component.translatable(this.getDescriptionId() + ".desc").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!level.isClientSide)
        {
            if (level instanceof ServerLevel serverLevel)
            {
                ResourceKey<LootTable> lootTableResourceKey = ResourceKey.create(Registries.LOOT_TABLE, lootTable);

                LootTable loot = serverLevel.getServer().reloadableRegistries().getLootTable(lootTableResourceKey);

                LootParams.Builder builder = new LootParams.Builder(serverLevel)
                        .withParameter(LootContextParams.THIS_ENTITY, player)
                        .withParameter(LootContextParams.ORIGIN, player.position())
                        .withParameter(LootContextParams.DAMAGE_SOURCE, player.damageSources().playerAttack(player))
                        .withOptionalParameter(LootContextParams.ATTACKING_ENTITY, player)
                        .withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, player)
                        // Adding luck cause why not
                        .withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player)
                        .withLuck(player.getLuck())
                        ;

                LootParams lootParams = builder.create(LootContextParamSets.ENTITY);

                List<ItemStack> actualLootItems = loot.getRandomItems(lootParams);

                for (ItemStack stack : actualLootItems)
                {
                    player.drop(stack, true);
                }

                if (particleTypes != null)
                {
                    ASUtils.spawnParticlesInCircle(8, 0.55F, 0.5F, 0.1F, player, particleTypes);
                } else
                {
                    ASUtils.spawnParticlesInCircle(8, 0.55F, 0.5F, 0.1F, player, ParticleTypes.HAPPY_VILLAGER);
                }
            }
        }

        // Remove loot bag if in survival
        if (!player.isCreative())
        {
            player.getMainHandItem().shrink(1);
        }
        level.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.BUNDLE_DROP_CONTENTS, SoundSource.PLAYERS, 1, 1, false);
        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }
}
