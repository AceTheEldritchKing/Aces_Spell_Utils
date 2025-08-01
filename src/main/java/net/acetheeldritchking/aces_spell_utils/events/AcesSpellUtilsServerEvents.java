package net.acetheeldritchking.aces_spell_utils.events;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.network.SyncManaPacket;
import net.acetheeldritchking.aces_spell_utils.registries.ASAttributeRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.network.PacketDistributor;

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
                float manaStealAttr = (float) serverPlayer.getAttributeValue(ASAttributeRegistry.MANA_STEAL);
                int maxAttackerMana = (int) serverPlayer.getAttributeValue(AttributeRegistry.MAX_MANA);
                var attackerPlayerMagicData = MagicData.getPlayerMagicData(serverPlayer);

                if (manaStealAttr > 0) // Prevents everyone from having Mana Steal by default
                {
                    int addMana = (int) Math.min((manaStealAttr * event.getOriginalDamage()) + attackerPlayerMagicData.getMana(), maxAttackerMana);

                    attackerPlayerMagicData.setMana(addMana);
                    PacketDistributor.sendToPlayer(serverPlayer, new SyncManaPacket(attackerPlayerMagicData));
                }

                if (target instanceof ServerPlayer serverTargetPlayer)
                {
                    int maxTargetMana = (int) serverTargetPlayer.getAttributeValue(AttributeRegistry.MAX_MANA);
                    var targetPlayerMagicData = MagicData.getPlayerMagicData(serverTargetPlayer);

                    int subMana = (int) Math.min((manaStealAttr * event.getOriginalDamage()) - attackerPlayerMagicData.getMana(), maxAttackerMana);

                    if (maxTargetMana > 0)
                    {
                        targetPlayerMagicData.setMana(subMana);
                        PacketDistributor.sendToPlayer(serverPlayer, new SyncManaPacket(targetPlayerMagicData));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingIncomingDamageEvent(LivingIncomingDamageEvent event)
    {
        var target = event.getEntity();
        var attacker = event.getSource().getEntity();

        // Attributes
        if (attacker instanceof LivingEntity livingEntity)
        {
            if (livingEntity instanceof ServerPlayer serverPlayer)
            {
                float manaRendAttr = (float) serverPlayer.getAttributeValue(ASAttributeRegistry.MANA_REND);

                var victim = event.getEntity();
                var victimMaxMana = victim.getAttributeValue(AttributeRegistry.MAX_MANA);
                var victimBaseMana = victim.getAttributeBaseValue(AttributeRegistry.MAX_MANA);

                //System.out.println("Start");
                if (manaRendAttr > 0 && victimMaxMana > victimBaseMana)
                {
                    //System.out.println("Eval attributes");
                    // Looking at how Betrayer Signet is done; we're adapting that math to us the attribute as the conversion ratio
                    // Rather than a flat 10%, it's whatever the attribute is
                    
                        //System.out.println("Doing the eval mana");
                        var manaAboveBase = victimMaxMana - victimBaseMana;

                        double conversionRationPer100 = manaRendAttr;
                        
                        if (!(manaAboveBase > 0 || conversionRationPer100 > 0)) return;
                        
                        var step = Math.clamp(manaAboveBase * 0.01, 0, 100);
                        double totalExtraDamagerPercent = 1 + (step * conversionRationPer100);
                        
                        event.setAmount((float) (event.getAmount() * Math.max(1, totalExtraDamagerPercent)));

                        //System.out.println("Old Damage" + event.getOriginalAmount());
                        //System.out.println("New Damage" + event.getAmount());
                }
            }
        }
    }
}
