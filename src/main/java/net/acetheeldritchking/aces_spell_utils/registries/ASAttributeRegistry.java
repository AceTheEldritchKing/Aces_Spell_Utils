package net.acetheeldritchking.aces_spell_utils.registries;

import net.acetheeldritchking.aces_spell_utils.AcesSpellUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.PercentageAttribute;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = AcesSpellUtils.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ASAttributeRegistry {
    private static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, AcesSpellUtils.MOD_ID);

    // Mana Steal
    public static final DeferredHolder<Attribute, Attribute> MANA_STEAL = registerAttribute("mana_steal");

    public static void register(IEventBus eventBus)
    {
        ATTRIBUTES.register(eventBus);
    }

    private static DeferredHolder<Attribute, Attribute> registerAttribute(String id)
    {
        return ATTRIBUTES.register(id, () -> new PercentageAttribute("attribute.aces_spell_utils." + id, 0.0D, 0.0D, 10.0D).setSyncable(true));
    }
}
