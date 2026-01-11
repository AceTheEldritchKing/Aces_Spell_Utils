package net.acetheeldritchking.aces_spell_utils.items.example;

import io.redspace.ironsspellbooks.api.item.weapons.ExtendedSwordItem;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.acetheeldritchking.aces_spell_utils.items.weapons.ActiveAndPassiveAbilitySwordItem;
import net.acetheeldritchking.aces_spell_utils.utils.ASRarities;

public class ExampleAPSwordItem extends ActiveAndPassiveAbilitySwordItem {
    public ExampleAPSwordItem() {
        super(
                ASSWeaponTiers.EXAMPLE_AP_SWORD,
                ItemPropertiesHelper.equipment(1).fireResistant().rarity(ASRarities.COSMIC_RARITY_PROXY.getValue()).attributes(ExtendedSwordItem.createAttributes(ASSWeaponTiers.EXAMPLE_AP_SWORD))
        );
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
