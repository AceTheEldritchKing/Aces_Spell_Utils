package net.acetheeldritchking.aces_spell_utils.utils;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

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
}
