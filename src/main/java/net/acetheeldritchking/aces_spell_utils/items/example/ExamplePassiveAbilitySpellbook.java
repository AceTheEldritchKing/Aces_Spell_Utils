package net.acetheeldritchking.aces_spell_utils.items.example;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.acetheeldritchking.aces_spell_utils.items.curios.PassiveAbilitySpellbook;
import net.acetheeldritchking.aces_spell_utils.utils.ASRarities;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class ExamplePassiveAbilitySpellbook extends PassiveAbilitySpellbook {
    public static final int COOLDOWN = 5 * 20;

    public ExamplePassiveAbilitySpellbook()
    {
        super(12, ItemPropertiesHelper.equipment().fireResistant().stacksTo(1).rarity(ASRarities.ARID_RARITY_PROXY.getValue()));
        withSpellbookAttributes(
                new AttributeContainer(AttributeRegistry.MAX_MANA, 300, AttributeModifier.Operation.ADD_VALUE),
                new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.25F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
        );
    }

    @Override
    protected int getCooldownTicks() {
        return COOLDOWN;
    }
}
