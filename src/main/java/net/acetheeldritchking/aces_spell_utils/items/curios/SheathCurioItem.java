package net.acetheeldritchking.aces_spell_utils.items.curios;

import io.redspace.ironsspellbooks.item.curios.PassiveAbilityCurio;

// Empty class, just for sheaths - Extend from this and override any methods
// File path for adding sheaths is as follows:
// curios -> tags -> item -> sheath.json
public abstract class SheathCurioItem extends PassiveAbilityCurio {
    public SheathCurioItem(Properties properties, String slotIdentifier) {
        super(properties, slotIdentifier);
    }
}
