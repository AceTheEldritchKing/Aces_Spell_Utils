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
    public static final DeferredHolder<Attribute, Attribute> MANA_STEAL = registerMagicPercentageAttribute("mana_steal", 0.0D, -100, 100.0D);

    // Mana Rend
    public static final DeferredHolder<Attribute, Attribute> MANA_REND = registerMagicPercentageAttribute("mana_rend", 0.0D, -100, 100.0D);

    // Goliath Slayer
    public static final DeferredHolder<Attribute, Attribute> GOLIATH_SLAYER = registerPercentageAttribute("goliath_slayer", 0.0D, -100, 100.0D);
    
    // Hunger Steal
    public static final DeferredHolder<Attribute, Attribute> HUNGER_STEAL = registerRangedAttribute("hunger_steal", 0.0, -100, 100.0D);

    // Spell Res Penetration
    public static final DeferredHolder<Attribute, Attribute> SPELL_RES_PENETRATION = registerPercentageAttribute("spell_res_penetration", 0, 0, 1.0);

    // Evasive
    public static final DeferredHolder<Attribute, Attribute> EVASIVE = registerRangedAttribute("evasive", 0, 0, 100.0);

    // Magic Damage Crit Chance
    public static final DeferredHolder<Attribute, Attribute> MAGIC_DAMAGE_CRIT_CHANCE = registerPercentageAttribute("magic_damage_crit_chance", 0.05D, 0.0D, 10.0D);

    // Magic Damage Crit Damage
    public static final DeferredHolder<Attribute, Attribute> MAGIC_DAMAGE_CRIT_DAMAGE = registerPercentageAttribute("magic_damage_crit_damage", 1.5D, 1.0D, 100.0D);

    // Magic Projectile Crit Chance
    public static final DeferredHolder<Attribute, Attribute> MAGIC_PROJECTILE_CRIT_CHANCE = registerPercentageAttribute("magic_projectile_crit_chance", 0.05D, 0.0D, 10.0D);

    // Magic Projectile Crit Damage
    public static final DeferredHolder<Attribute, Attribute> MAGIC_PROJECTILE_CRIT_DAMAGE = registerPercentageAttribute("magic_projectile_crit_damage", 1.5D, 1.0D, 100.0D);

    // Magic Projectile Bonus Damage
    public static final DeferredHolder<Attribute, Attribute> MAGIC_PROJECTILE_DAMAGE = registerPercentageAttribute("magic_projectile_damage", 0, 0, 1.0);

    // Life Recovery
    public static final DeferredHolder<Attribute, Attribute> LIFE_RECOVERY = registerPercentageAttribute("life_recovery", 0, 0, 1.0);


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
