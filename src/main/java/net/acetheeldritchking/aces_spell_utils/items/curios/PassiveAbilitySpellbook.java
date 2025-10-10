package net.acetheeldritchking.aces_spell_utils.items.curios;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import java.util.List;

public class PassiveAbilitySpellbook extends SpellBook {
    Style description;
    protected final int maxSpellSlots;

    public int getMaxSpellSlots() {
        return maxSpellSlots;
    }

    public PassiveAbilitySpellbook() {
        this(1);
        description = Style.EMPTY.withColor(ChatFormatting.YELLOW);
    }

    public PassiveAbilitySpellbook(int maxSpellSlots) {
        this(maxSpellSlots, ItemPropertiesHelper.equipment().stacksTo(1).rarity(Rarity.UNCOMMON));
        description = Style.EMPTY.withColor(ChatFormatting.YELLOW);
    }

    public PassiveAbilitySpellbook(int maxSpellSlots, Item.Properties pProperties) {
        super(maxSpellSlots, pProperties);
        this.maxSpellSlots = maxSpellSlots;
        description = Style.EMPTY.withColor(ChatFormatting.YELLOW);
    }

    @Override
    public List<Component> getAttributesTooltip(List<Component> tooltips, TooltipContext context, ItemStack stack) {
        var tooltip = super.getAttributesTooltip(tooltips, context, stack);
        boolean needsHeader = tooltip.isEmpty();
        var descLines = getDescriptionLines(stack);
        if (needsHeader && !descLines.isEmpty())
        {
            tooltip.add(Component.empty());
        }
        tooltip.addAll(descLines);

        return tooltip;
    }

    protected int getCooldownTicks() {
        return 0;
    }

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

    public List<Component> getDescriptionLines(ItemStack stack)
    {
        return List.of(
                Component.translatable(
                        "tooltip.irons_spellbooks.passive_ability",
                        Component.literal(Utils.timeFromTicks(getCooldownTicks(), 1)).withStyle(ChatFormatting.AQUA)
                ).withStyle(ChatFormatting.GREEN),
                getDescription(stack)
        );
    }

    public Component getDescription(ItemStack stack) {
        return Component.literal(" ").append(Component.translatable(this.getDescriptionId() + ".desc")).withStyle(description);
    }
}
