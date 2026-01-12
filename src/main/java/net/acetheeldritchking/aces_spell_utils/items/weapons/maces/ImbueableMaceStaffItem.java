package net.acetheeldritchking.aces_spell_utils.items.weapons.maces;

import io.redspace.ironsspellbooks.api.spells.IPresetSpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import net.minecraft.world.item.ItemStack;

public class ImbueableMaceStaffItem extends MaceStaffItem implements IPresetSpellContainer {
    public ImbueableMaceStaffItem(Properties properties) {
        super(properties);
    }

    @Override
    public void initializeSpellContainer(ItemStack itemStack) {
        if (itemStack == null)
        {
            return;
        }

        if (itemStack.getItem() instanceof MaceStaffItem maceItem)
        {
            if (!ISpellContainer.isSpellContainer(itemStack))
            {
                var spellContainer = ISpellContainer.create(1, true, false);
                ISpellContainer.set(itemStack, spellContainer);
            }
        }
    }
}
