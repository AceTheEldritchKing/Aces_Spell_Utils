package net.acetheeldritchking.aces_spell_utils.items.curios;

// Empty class, just for sheaths - Extend from this and override any methods
// File path for adding sheaths is as follows:
// curios -> tags -> item -> sheath.json
public abstract class SheathCurioItem extends FlatCooldownPassiveAbilityCurio {
    public SheathCurioItem(Properties properties, String slotIdentifier) {
        super(properties, slotIdentifier);
    }
}
