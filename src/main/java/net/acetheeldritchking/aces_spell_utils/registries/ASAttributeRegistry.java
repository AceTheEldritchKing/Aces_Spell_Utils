package net.acetheeldritchking.aces_spell_utils.registries;

import io.redspace.ironsspellbooks.api.attribute.MagicPercentAttribute;
import io.redspace.ironsspellbooks.api.attribute.MagicRangedAttribute;
import net.acetheeldritchking.aces_spell_utils.AcesSpellUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.PercentageAttribute;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = AcesSpellUtils.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ASAttributeRegistry {
    private static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, AcesSpellUtils.MOD_ID);
    
    // Mana Steal
    public static final DeferredHolder<Attribute, Attribute> MANA_STEAL = registerMagicRangedAttribute("mana_steal", 0.0D, -100, 100.0D);

    // Mana Rend
    public static final DeferredHolder<Attribute, Attribute> MANA_REND = registerMagicRangedAttribute("mana_rend", 0.0D, -100, 100.0D);

    // Goliath Slayer
    public static final DeferredHolder<Attribute, Attribute> GOLIATH_SLAYER = registerMagicRangedAttribute("goliath_slayer", 0.0D, -100, 100.0D);
    
    // Hunger Steal
    public static final DeferredHolder<Attribute, Attribute> HUNGER_STEAL = registerPercentageAttribute("hunger_steal", 0.0, 0, 1.0);

    // Spell Res Shred
    //public static final DeferredHolder<Attribute, Attribute> SPELL_RES_PENETRATION = registerMagicPercentageAttribute("spell_res_penetration", 0, 0, 1);

    // Hunger Steal
    //public static final DeferredHolder<Attribute, Attribute> HUNGER_STEAL = registerMagicRangedAttribute("hunger_steal", 0.0D, -100, 100.0D);


    public static void register(IEventBus eventBus)
    {
        ATTRIBUTES.register(eventBus);
    }

    @SubscribeEvent
    public static void modifyEntityAttributes(EntityAttributeModificationEvent event)
    {
        event.getTypes().forEach(entityType ->
                ATTRIBUTES.getEntries().forEach(
                        attributeDeferredHolder -> event.add(entityType, attributeDeferredHolder
                        )));
    }

    private static DeferredHolder<Attribute, Attribute> registerMagicRangedAttribute(String id, double defaultVal, double minVal, double maxVal)
    {
        return ATTRIBUTES.register(id, () ->
                (new MagicRangedAttribute("attribute.aces_spell_utils." + id,
                        defaultVal, minVal, maxVal).setSyncable(true)));
    }

    private static DeferredHolder<Attribute, Attribute> registerMagicPercentageAttribute(String id, double defaultVal, double minVal, double maxVal)
    {
        return ATTRIBUTES.register(id, () ->
                (new MagicPercentAttribute("attribute.aces_spell_utils." + id,
                        defaultVal, minVal, maxVal).setSyncable(true)));
    }

    private static DeferredHolder<Attribute, Attribute> registerRangedAttribute(String id, double defaultVal, double minVal, double maxVal)
    {
        return ATTRIBUTES.register(id, () ->
                (new RangedAttribute("attribute.aces_spell_utils." + id,
                        defaultVal, minVal, maxVal).setSyncable(true)));
    }

    private static DeferredHolder<Attribute, Attribute> registerPercentageAttribute(String id, double defaultVal, double minVal, double maxVal)
    {
        return ATTRIBUTES.register(id, () ->
                (new PercentageAttribute("attribute.aces_spell_utils." + id,
                        defaultVal, minVal, maxVal).setSyncable(true)));
    }
}
