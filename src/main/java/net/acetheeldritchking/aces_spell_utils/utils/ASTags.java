package net.acetheeldritchking.aces_spell_utils.utils;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.acetheeldritchking.aces_spell_utils.AcesSpellUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public class ASTags {
    /***
     * Spell Tags
     */
    public static TagKey<AbstractSpell> STOMP_LIKE_SPELL = createSpellTag(AcesSpellUtils.id("stomp_like_spell"));
    public static TagKey<AbstractSpell> SLASH_LIKE_SPELL = createSpellTag(AcesSpellUtils.id("slash_like_spell"));

    public static TagKey<AbstractSpell> createSpellTag(ResourceLocation tag)
    {
        return new TagKey<AbstractSpell>(SpellRegistry.SPELL_REGISTRY_KEY, tag);
    }
}
