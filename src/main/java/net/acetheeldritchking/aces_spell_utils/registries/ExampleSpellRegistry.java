package net.acetheeldritchking.aces_spell_utils.registries;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.acetheeldritchking.aces_spell_utils.AcesSpellUtils;
import net.acetheeldritchking.aces_spell_utils.spells.examples.ExampleAdvancedTPSpell;
import net.acetheeldritchking.aces_spell_utils.spells.examples.ExampleExtendedSpell;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static io.redspace.ironsspellbooks.api.registry.SpellRegistry.SPELL_REGISTRY_KEY;

public class ExampleSpellRegistry {
    public static final DeferredRegister<AbstractSpell> SPELLS = DeferredRegister.create(SPELL_REGISTRY_KEY, AcesSpellUtils.MOD_ID);

    public static Supplier<AbstractSpell> registerSpell(AbstractSpell spell) {
        return SPELLS.register(spell.getSpellName(), () -> spell);
    }

    public static final Supplier<AbstractSpell> EXAMPLE_ADVANCED_SPELL = registerSpell(new ExampleExtendedSpell());

    public static final Supplier<AbstractSpell> EXAMPLE_ADVANCED_TP_SPELL = registerSpell(new ExampleAdvancedTPSpell());

    public static void register(IEventBus eventBus)
    {
        SPELLS.register(eventBus);
    }
}
