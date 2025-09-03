package net.acetheeldritchking.aces_spell_utils.items.example;

import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.compat.Curios;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.acetheeldritchking.aces_spell_utils.items.curios.ImbuableCurio;

public class ExampleCurioItem extends ImbuableCurio {
    public ExampleCurioItem() {
        super(ItemPropertiesHelper.equipment().stacksTo(1).fireResistant(), Curios.NECKLACE_SLOT,
                SpellDataRegistryHolder.of(new SpellDataRegistryHolder(SpellRegistry.RAISE_DEAD_SPELL, 10)));
    }
}
