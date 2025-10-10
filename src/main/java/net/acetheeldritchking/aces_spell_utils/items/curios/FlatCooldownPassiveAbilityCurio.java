package net.acetheeldritchking.aces_spell_utils.items.curios;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.item.curios.SimpleDescriptiveCurio;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public abstract class FlatCooldownPassiveAbilityCurio extends SimpleDescriptiveCurio {
    public FlatCooldownPassiveAbilityCurio(Properties properties, String slotIdentifier) {
        super(properties, slotIdentifier);
    }

    protected abstract int getCooldownTicks();

    public boolean tryProcCooldown(Player player)
    {
        if (player.getCooldowns().isOnCooldown(this))
        {
            return false;
        } else
        {
            player.getCooldowns().addCooldown(this, getCooldownTicks());
            return true;
        }
    }

    @Override
    public List<Component> getDescriptionLines(ItemStack stack) {

        return List.of(
                Component.translatable(
                        "tooltip.irons_spellbooks.passive_ability",
                        Component.literal(Utils.timeFromTicks(getCooldownTicks(), 1)).withStyle(ChatFormatting.AQUA)
                ).withStyle(ChatFormatting.GREEN),
                getDescription(stack)
        );
    }
}
