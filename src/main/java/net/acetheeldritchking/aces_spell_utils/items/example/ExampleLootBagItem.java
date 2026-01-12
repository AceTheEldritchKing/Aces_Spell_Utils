package net.acetheeldritchking.aces_spell_utils.items.example;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.render.CinderousRarity;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.acetheeldritchking.aces_spell_utils.items.custom.LootBagItem;
import net.minecraft.resources.ResourceLocation;

public class ExampleLootBagItem extends LootBagItem {
    static ResourceLocation lootTable = ResourceLocation.fromNamespaceAndPath(IronsSpellbooks.MODID, "entities/fire_boss");

    public ExampleLootBagItem() {
        super(
                ItemPropertiesHelper.equipment(8).fireResistant().rarity(CinderousRarity.CINDEROUS_RARITY_PROXY.getValue()),
                lootTable
        );
    }
}
