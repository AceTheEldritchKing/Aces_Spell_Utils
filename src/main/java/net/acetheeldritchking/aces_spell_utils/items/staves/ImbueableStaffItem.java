package net.acetheeldritchking.aces_spell_utils.items.staves;

import io.redspace.ironsspellbooks.api.spells.IPresetSpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.item.weapons.StaffItem;
import net.minecraft.world.item.ItemStack;

public class ImbueableStaffItem extends StaffItem implements IPresetSpellContainer {
    public ImbueableStaffItem(Properties properties) {
        super(properties);
    }

    @Override
    public void initializeSpellContainer(ItemStack itemStack) {
        if (itemStack == null)
        {
            return;
        }

        if (itemStack.getItem() instanceof StaffItem staffItem)
        {
            if (!ISpellContainer.isSpellContainer(itemStack))
            {
                var spellContainer = ISpellContainer.create(1, true, false);
                ISpellContainer.set(itemStack, spellContainer);
            }
        }
    }
}
