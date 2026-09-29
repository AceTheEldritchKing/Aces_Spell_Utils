package net.acetheeldritchking.aces_spell_utils.items.example.items.staves;

import io.redspace.ironsspellbooks.api.item.weapons.ExtendedSwordItem;
import net.acetheeldritchking.aces_spell_utils.items.staves.ImbueableStaffItem;
import net.acetheeldritchking.aces_spell_utils.utils.ASRarities;

public class ExampleImbueStaffItem extends ImbueableStaffItem {
    public ExampleImbueStaffItem() {
        super(
                new Properties().stacksTo(1).fireResistant().rarity(ASRarities.FORBIDDEN_RARITY_PROXY.getValue()).attributes(ExtendedSwordItem.createAttributes(ASStaffTier.EXAMPLE_STAFF_TWO))
        );
    }
}
