package net.acetheeldritchking.aces_spell_utils.utils;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.List;

public class ASUtils {
    // Gets equipped curio on the player
    public static boolean hasCurio(Player player, Item item)
    {
        return CuriosApi.getCuriosHelper().findEquippedCurio(item, player).isPresent();
    }

    // Checks if an entity is doing a long cast spell
    public static boolean isLongAnimCast(AbstractSpell spell)
    {
        if (spell.getCastType() == CastType.LONG)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    // Checks if an entity is doing a continuous cast spell
    public static boolean isContAnimCast(AbstractSpell spell)
    {
        if (spell.getCastType() == CastType.CONTINUOUS)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    // Get spells from a tag
    public static List<AbstractSpell> getSpellsFromTag(TagKey<AbstractSpell> tag)
    {
        var list = new ArrayList<AbstractSpell>();

        for (var spell : SpellRegistry.getEnabledSpells())
        {
            SpellRegistry.REGISTRY.getHolder(spell.getSpellResource()).ifPresent(
                    s -> {
                        if (s.is(tag))
                        {
                            list.add(spell);
                        }
                    }
            );
        }

        return list;
    }

    // Circle of particles
    public static void spawnParticlesInCircle(int count, float radius, float yHeight, float particleSpeed, LivingEntity entity, ParticleOptions particleTypes)
    {
        for (int i = 0; i < count; ++i)
        {
            double theta = Math.toRadians(360/count) * i;
            double x = Math.cos(theta) * radius;
            double z = Math.sin(theta) * radius;

            MagicManager.spawnParticles(entity.level(), particleTypes,
                    entity.position().x + x,
                    entity.position().y + yHeight,
                    entity.position().z + z,
                    1,
                    0,
                    0,
                    0,
                    particleSpeed,
                    false);
        }
    }

    // Three rings of particles
    public static void spawnParticlesInRing(int count, float radius1, float radius2, float radius3, float yHeight, float particleSpeed, LivingEntity entity, ParticleOptions particleTypes)
    {
        // Ring 1
        for (int i = 0; i < count; ++i)
        {
            double theta = Math.toRadians(360/count) * i;
            double x = Math.cos(theta) * radius1;
            double z = Math.sin(theta) * radius1;

            MagicManager.spawnParticles(entity.level(), particleTypes,
                    entity.position().x + x,
                    entity.position().y + yHeight,
                    entity.position().z + z,
                    1,
                    0,
                    0,
                    0,
                    particleSpeed,
                    false);
        }

        // Ring 2
        for (int i = 0; i < count; ++i)
        {
            double theta = Math.toRadians(360/count) * i;
            double x = Math.cos(theta) * radius2;
            double z = Math.sin(theta) * radius2;

            MagicManager.spawnParticles(entity.level(), particleTypes,
                    entity.position().x + x,
                    entity.position().y + yHeight,
                    entity.position().z + z,
                    1,
                    0,
                    0,
                    0,
                    particleSpeed,
                    false);
        }

        // Ring 3
        for (int i = 0; i < count; ++i)
        {
            double theta = Math.toRadians(360/count) * i;
            double x = Math.cos(theta) * radius3;
            double z = Math.sin(theta) * radius3;

            MagicManager.spawnParticles(entity.level(), particleTypes,
                    entity.position().x + x,
                    entity.position().y + yHeight,
                    entity.position().z + z,
                    1,
                    0,
                    0,
                    0,
                    particleSpeed,
                    false);
        }
    }

    // Formated Ticks to Time
    public static String convertTicksToTime(int ticks) {
        // Convert ticks to seconds
        int totalSeconds = ticks / 20;

        // Calculate minutes and seconds
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        // Format the result as mm:ss
        return String.format("%02d:%02d" , minutes , seconds);
    }

    // Get caster eye height, pretty much what it says
    public static double getEyeHeight(LivingEntity entity)
    {
        return entity.getY() + entity.getEyeHeight() - 0.2;
    }

    // For unlocking spells using a specific item
    public static boolean isValidUnlockItemInInventory(Item item, Player player)
    {
        for (int i = 0; i < player.getInventory().getContainerSize(); ++i)
        {
            ItemStack itemStack = player.getInventory().getItem(i);
            if (itemStack.getItem() == item)
            {
                return true;
            }
        }

        return false;
    }

    // Unlocking spells and consuming an item
    public static boolean isValidConsumableUnlockItemInInventory(Item item, Player player)
    {
        for (int i = 0; i < player.getInventory().getContainerSize(); ++i)
        {
            ItemStack itemStack = player.getInventory().getItem(i);
            if (itemStack.getItem() == item)
            {
                itemStack.shrink(1);
                return true;
            }
        }

        return false;
    }

    // Scale damage based on other attributes
    // They also scale off of spell power as these are intended to be used for ISS spells, but they can be used for other things as well
    // If an entity has no spell power, it shouldn't really affect the math much
    public static float getDamageForAttributes(LivingEntity entity, Holder<Attribute> attr1, Holder<Attribute> attr2, float modifier)
    {
        double attrValue1 = entity.getAttributeValue(attr1);
        double attrValue2 = entity.getAttributeValue(attr2);
        double spellPower = entity.getAttributeValue(AttributeRegistry.SPELL_POWER);

        float damage = (float) (modifier * (attrValue1 + attrValue2 + spellPower));

        return damage;
    }

    public static float getDamageForAttributes(LivingEntity entity, Holder<Attribute> attr1, Holder<Attribute> attr2, Holder<Attribute> attr3, float modifier)
    {
        double attrValue1 = entity.getAttributeValue(attr1);
        double attrValue2 = entity.getAttributeValue(attr2);
        double attrValue3 = entity.getAttributeValue(attr3);
        double spellPower = entity.getAttributeValue(AttributeRegistry.SPELL_POWER);

        float damage = (float) (modifier * (attrValue1 + attrValue2 + attrValue3 + spellPower));

        return damage;
    }

    public static float getDamageForAttributes(LivingEntity entity, Holder<Attribute> attr1, Holder<Attribute> attr2, Holder<Attribute> attr3, Holder<Attribute> attr4, float modifier)
    {
        double attrValue1 = entity.getAttributeValue(attr1);
        double attrValue2 = entity.getAttributeValue(attr2);
        double attrValue3 = entity.getAttributeValue(attr3);
        double attrValue4 = entity.getAttributeValue(attr4);
        double spellPower = entity.getAttributeValue(AttributeRegistry.SPELL_POWER);

        float damage = (float) (modifier * (attrValue1 + attrValue2 + attrValue3 + attrValue4 + spellPower));

        return damage;
    }

    // Took this from Ender with his permission
    // Detects when an entity is under the sun
    public static boolean isUnderTheSun(Level level, LivingEntity entity)
    {
        if (level.isDay() && !level.isClientSide)
        {
            float light = entity.getLightLevelDependentMagicValue();
            BlockPos blockPos = BlockPos.containing(entity.getX(), entity.getEyeY(), entity.getZ());

            boolean flag = entity.isInWaterRainOrBubble() || entity.isInPowderSnow || entity.wasInPowderSnow;

            if (light > 0.5F && !flag && level.canSeeSky(blockPos))
            {
                return true;
            }
        }

        return false;
    }
}
