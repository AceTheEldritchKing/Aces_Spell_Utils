package net.acetheeldritchking.aces_spell_utils.events;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.acetheeldritchking.aces_spell_utils.registries.ASAttributeRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber
public class AcesSpellUtilsServerEvents {
    @SubscribeEvent
    public static void livingDamageEvent(LivingDamageEvent.Post event)
    {
        var sourceEntity = event.getSource().getEntity();
        var target = event.getEntity();
        var projectile = event.getSource().getDirectEntity();

        // Attributes
        if (sourceEntity instanceof LivingEntity livingEntity)
        {
            if (livingEntity instanceof ServerPlayer serverPlayer)
            {
                double manaSteal = serverPlayer.getAttributeValue(ASAttributeRegistry.MANA_STEAL);
                int maxAttackerMana = (int) serverPlayer.getAttributeValue(AttributeRegistry.MAX_MANA);
                var attackerPlayerMagicData = MagicData.getPlayerMagicData(serverPlayer);

                int addMana = (int) Math.min(manaSteal + attackerPlayerMagicData.getMana(), maxAttackerMana);

                attackerPlayerMagicData.setMana(addMana);

                if (target instanceof ServerPlayer serverTargetPlayer)
                {
                    int maxTargetMana = (int) serverTargetPlayer.getAttributeValue(AttributeRegistry.MAX_MANA);
                    var targetPlayerMagicData = MagicData.getPlayerMagicData(serverTargetPlayer);

                    int subMana = (int) Math.min(manaSteal + attackerPlayerMagicData.getMana(), maxAttackerMana);

                    if (maxTargetMana > 0)
                    {
                        targetPlayerMagicData.setMana(subMana);
                    }
                }
            }
        }
    }
}
