package net.acetheeldritchking.aces_spell_utils.utils;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.acetheeldritchking.aces_spell_utils.AcesSpellUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class ASTags {
    /***
     * Item Tags
     */

    /***
     * Entity Tags
     */
    // Apothic Cultists
    public static final TagKey<EntityType<?>> BOSS_LIKE_ENTITES = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(AcesSpellUtils.MOD_ID, "boss_like_entities"));

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
