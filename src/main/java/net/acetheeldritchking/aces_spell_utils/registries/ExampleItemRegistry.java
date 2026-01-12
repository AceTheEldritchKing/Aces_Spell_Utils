package net.acetheeldritchking.aces_spell_utils.registries;

import io.redspace.ironsspellbooks.item.curios.CurioBaseItem;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.acetheeldritchking.aces_spell_utils.AcesSpellUtils;
import net.acetheeldritchking.aces_spell_utils.items.example.*;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collection;
import java.util.function.Supplier;

public class ExampleItemRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AcesSpellUtils.MOD_ID);

    // Example Sheath
    public static final Supplier<CurioBaseItem> EXAMPLE_SHEATH = ITEMS.register("example_sheath", ExampleSheathCurioItem::new);

    // Example Preset Staff
    public static final DeferredHolder<Item, Item> EXAMPLE_STAFF = ITEMS.register("example_staff", ExamplePresetStaffItem::new);

    // Example Preset Curio
    public static final Supplier<CurioBaseItem> EXAMPLE_CURIO = ITEMS.register("example_curio", ExamplePresetCurioItem::new);

    // Example Imbue Staff
    public static final DeferredHolder<Item, Item> EXAMPLE_IMBUE_STAFF = ITEMS.register("example_imbue_staff", ExampleImbueStaffItem::new);

    // Example Imbue Curio
    public static final Supplier<CurioBaseItem> EXAMPLE_IMBUE_CURIO = ITEMS.register("example_imbue_curio", ExampleImbueCurioItem::new);

    // Example Passive Ability Spellbook
    public static final DeferredHolder<Item, Item> EXAMPLE_PASSIVE_ABILITY_SPELLBOOK = ITEMS.register("example_passive_ability_spellbook", ExamplePassiveAbilitySpellbook::new);

    // Example Magic Gun
    public static final DeferredHolder<Item, Item> EXAMPLE_GUN = ITEMS.register("example_gun", ExampleGunItem::new);

    // Example A&P Sword
    public static final DeferredHolder<Item, Item> EXAMPLE_AP_SWORD = ITEMS.register("example_ap_sword", ExampleAPSwordItem::new);

    // Example A&P Magic Sword
    public static final DeferredHolder<Item, Item> EXAMPLE_AP_MAGIC_SWORD = ITEMS.register("example_ap_magic_sword", ExampleAPMagicSwordItem::new);

    // Example Loot Bag Item
    public static final DeferredHolder<Item, Item> EXAMPLE_LOOT_BAG = ITEMS.register("example_loot_bag", ExampleLootBagItem::new);

    // Armor
    public static final DeferredHolder<Item, Item> EXAMPLE_ARMOR_HELMET = ITEMS.register("example_armor_helmet", () -> new ExampleWarlockArmorItem(ArmorItem.Type.HELMET, ItemPropertiesHelper.equipment(1).fireResistant().durability(ArmorItem.Type.HELMET.getDurability(40))));
    public static final DeferredHolder<Item, Item> EXAMPLE_ARMOR_CHESTPLATE = ITEMS.register("example_armor_chestplate", () -> new ExampleWarlockArmorItem(ArmorItem.Type.CHESTPLATE, ItemPropertiesHelper.equipment(1).fireResistant().durability(ArmorItem.Type.CHESTPLATE.getDurability(40))));
    public static final DeferredHolder<Item, Item> EXAMPLE_ARMOR_LEGGINGS = ITEMS.register("example_armor_leggings", () -> new ExampleWarlockArmorItem(ArmorItem.Type.LEGGINGS, ItemPropertiesHelper.equipment(1).fireResistant().durability(ArmorItem.Type.LEGGINGS.getDurability(40))));
    public static final DeferredHolder<Item, Item> EXAMPLE_ARMOR_BOOTS = ITEMS.register("example_armor_boots", () -> new ExampleWarlockArmorItem(ArmorItem.Type.BOOTS, ItemPropertiesHelper.equipment(1).fireResistant().durability(ArmorItem.Type.BOOTS.getDurability(40))));


    public static Collection<DeferredHolder<Item, ? extends Item>> getASUItems()
    {
        return ITEMS.getEntries();
    }

    public static void register(IEventBus eventBus)
    {
        ITEMS.register(eventBus);
    }
}
