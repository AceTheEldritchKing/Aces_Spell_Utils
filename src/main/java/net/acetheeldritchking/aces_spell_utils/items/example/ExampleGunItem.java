package net.acetheeldritchking.aces_spell_utils.items.example;

import io.redspace.ironsspellbooks.api.item.weapons.ExtendedSwordItem;
import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.acetheeldritchking.aces_spell_utils.items.weapons.MagicGunItem;
import net.acetheeldritchking.aces_spell_utils.utils.ASRarities;

public class ExampleGunItem extends MagicGunItem {
    public ExampleGunItem() {
        super(
                ASWeaponTiers.EXAMPLE_GUN,
                ItemPropertiesHelper.equipment(1).fireResistant().rarity(ASRarities.AQUATIC_RARITY_PROXY.getValue()).attributes(ExtendedSwordItem.createAttributes(ASWeaponTiers.EXAMPLE_GUN)),
                SpellDataRegistryHolder.of(
                        new SpellDataRegistryHolder(SpellRegistry.BALL_LIGHTNING_SPELL, 10)
                )
        );
    }

    @Override
    public boolean isHeavyGun() {
        return true;
    }

    @Override
    protected int getPassiveCooldownTicks() {
        return 10 * 20;
    }

    @Override
    protected int getActiveCooldownTicks() {
        return 10 * 20;
    }
}
