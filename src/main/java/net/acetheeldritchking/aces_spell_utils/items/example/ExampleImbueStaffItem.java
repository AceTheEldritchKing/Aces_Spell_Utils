package net.acetheeldritchking.aces_spell_utils.items.example;

import io.redspace.ironsspellbooks.api.item.weapons.ExtendedSwordItem;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.acetheeldritchking.aces_spell_utils.items.staves.ImbueableStaffItem;
import net.acetheeldritchking.aces_spell_utils.utils.ASRarities;

public class ExampleImbueStaffItem extends ImbueableStaffItem {
    public ExampleImbueStaffItem() {
        super(
                ItemPropertiesHelper.equipment(1).fireResistant().rarity(ASRarities.FORBIDDEN_RARITY_PROXY.getValue()).attributes(ExtendedSwordItem.createAttributes(ASStaffTier.EXAMPLE_STAFF_TWO))
        );
    }
}
