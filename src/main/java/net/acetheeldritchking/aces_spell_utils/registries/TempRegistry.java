package net.acetheeldritchking.aces_spell_utils.registries;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.acetheeldritchking.aces_spell_utils.AcesSpellUtils;
import net.acetheeldritchking.aces_spell_utils.spells.ExampleSummonSpell;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static io.redspace.ironsspellbooks.api.registry.SpellRegistry.SPELL_REGISTRY_KEY;

public class TempRegistry {
    public static final DeferredRegister<AbstractSpell> SPELLS = DeferredRegister.create(SPELL_REGISTRY_KEY, AcesSpellUtils.MOD_ID);

    public static Supplier<AbstractSpell> registerSpell(AbstractSpell spell) {
        return SPELLS.register(spell.getSpellName(), () -> spell);
    }

    // example
    public static final Supplier<AbstractSpell> SILENCE = registerSpell(new ExampleSummonSpell());

    public static void register(IEventBus eventBus)
    {
        SPELLS.register(eventBus);
    }
}
