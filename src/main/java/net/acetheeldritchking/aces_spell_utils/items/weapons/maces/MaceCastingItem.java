package net.acetheeldritchking.aces_spell_utils.items.weapons.maces;

import io.redspace.ironsspellbooks.registries.ComponentRegistry;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;

public class MaceCastingItem extends MaceItem {
    public MaceCastingItem(Properties properties) {
        super(properties.component(ComponentRegistry.CASTING_IMPLEMENT, Unit.INSTANCE));
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }
}
