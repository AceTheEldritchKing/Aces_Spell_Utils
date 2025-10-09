package net.acetheeldritchking.aces_spell_utils.registries;

import net.acetheeldritchking.aces_spell_utils.AcesSpellUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ASCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AcesSpellUtils.MOD_ID);

    public static final Supplier<CreativeModeTab> ASU_ITEMS_TAB = CREATIVE_MODE_TAB.register("asu_item_tabs",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ExampleItemRegistry.EXAMPLE_SHEATH.get()))
                    .title(Component.translatable("creative_tab.aces_spell_utils.items"))
                    .displayItems((itemDisplayParameters, output) -> {
                        // Items
                        output.accept(ExampleItemRegistry.EXAMPLE_SHEATH.get());
                        output.accept(ExampleItemRegistry.EXAMPLE_STAFF.get());
                        output.accept(ExampleItemRegistry.EXAMPLE_IMBUE_STAFF.get());
                        output.accept(ExampleItemRegistry.EXAMPLE_CURIO.get());
                        output.accept(ExampleItemRegistry.EXAMPLE_IMBUE_CURIO.get());
                    }).build());

    public static void register(IEventBus eventBus)
    {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
