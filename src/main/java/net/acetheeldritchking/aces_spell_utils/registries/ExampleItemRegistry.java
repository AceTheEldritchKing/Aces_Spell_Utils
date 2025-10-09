package net.acetheeldritchking.aces_spell_utils.registries;

import io.redspace.ironsspellbooks.item.curios.CurioBaseItem;
import net.acetheeldritchking.aces_spell_utils.AcesSpellUtils;
import net.acetheeldritchking.aces_spell_utils.items.example.*;
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


    public static Collection<DeferredHolder<Item, ? extends Item>> getASUItems()
    {
        return ITEMS.getEntries();
    }

    public static void register(IEventBus eventBus)
    {
        ITEMS.register(eventBus);
    }
}
