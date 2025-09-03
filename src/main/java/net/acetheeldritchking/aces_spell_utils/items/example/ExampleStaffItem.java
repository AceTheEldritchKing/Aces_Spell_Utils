package net.acetheeldritchking.aces_spell_utils.items.example;

import io.redspace.ironsspellbooks.api.item.weapons.ExtendedSwordItem;
import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.acetheeldritchking.aces_spell_utils.items.staves.ImbuableStaffItem;
import net.acetheeldritchking.aces_spell_utils.utils.ASRarities;

public class ExampleStaffItem extends ImbuableStaffItem {
    public ExampleStaffItem() {
        super(
                ItemPropertiesHelper.equipment(1).fireResistant().rarity(ASRarities.COSMIC_RARITY_PROXY.getValue()).attributes(ExtendedSwordItem.createAttributes(ASStaffTier.EXAMPLE_STAFF)),
                SpellDataRegistryHolder.of(
                        new SpellDataRegistryHolder(SpellRegistry.BLACK_HOLE_SPELL, 6),
                        new SpellDataRegistryHolder(SpellRegistry.STARFALL_SPELL, 10))
        );
    }
}
