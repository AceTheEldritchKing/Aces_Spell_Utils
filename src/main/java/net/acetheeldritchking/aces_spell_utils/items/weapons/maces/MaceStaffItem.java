package net.acetheeldritchking.aces_spell_utils.items.weapons.maces;

import net.minecraft.world.item.ItemStack;

public class MaceStaffItem extends MaceCastingItem{
    public MaceStaffItem(Properties properties) {
        super(properties);
    }

    public boolean isEnchantable(ItemStack pStack) {
        return true;
    }

    public int getEnchantmentValue(ItemStack stack) {
        return 20;
    }

    public boolean hasCustomRendering() {
        return false;
    }
}
