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
    public static void livingDamageEvent(LivingDamageEvent.Post event) {
        var sourceEntity = event.getSource().getEntity();
        var target = event.getEntity();
        var projectile = event.getSource().getDirectEntity();

        //Safety checks - only works if user is a player
        if (!(sourceEntity instanceof LivingEntity livingEntity)) return;
        if (!(livingEntity instanceof ServerPlayer serverPlayer)) return;

        var hasManaSteal = serverPlayer.getAttribute(ASAttributeRegistry.MANA_STEAL);

        //Check if user has mana steal
        if (hasManaSteal == null) return;

        float manaStealAttr = (float) serverPlayer.getAttributeValue(ASAttributeRegistry.MANA_STEAL);
        int maxAttackerMana = (int) serverPlayer.getAttributeValue(AttributeRegistry.MAX_MANA);
        var attackerPlayerMagicData = MagicData.getPlayerMagicData(serverPlayer);

        //Check if user has Mana Steal
        if (manaStealAttr <= 0) return;
        int addMana = (int) Math.min((manaStealAttr * event.getOriginalDamage()) + attackerPlayerMagicData.getMana(), maxAttackerMana);

        //Returns mana "stolen"
        attackerPlayerMagicData.setMana(addMana);
        PacketDistributor.sendToPlayer(serverPlayer, new SyncManaPacket(attackerPlayerMagicData));

        //Check if target is a player for reducing their mana
        if (target instanceof ServerPlayer serverTargetPlayer) {
            int maxTargetMana = (int) serverTargetPlayer.getAttributeValue(AttributeRegistry.MAX_MANA);
            var targetPlayerMagicData = MagicData.getPlayerMagicData(serverTargetPlayer);

            int subMana = (int) Math.min((manaStealAttr * event.getOriginalDamage()) - attackerPlayerMagicData.getMana(), maxAttackerMana);

            //Final check for applying Mana Steal
            if (maxTargetMana <= 0) return;

            //Reduces target player's mana
            targetPlayerMagicData.setMana(subMana);
            PacketDistributor.sendToPlayer(serverPlayer, new SyncManaPacket(targetPlayerMagicData));
        }
    }

    @SubscribeEvent
    public static void manaRendEvent(LivingIncomingDamageEvent event) {
        //Grab involved entities
        var victim = event.getEntity();
        var attacker = event.getSource().getEntity();

        //Cancels modification if user isn't a living entity
        if (!(attacker instanceof LivingEntity livingEntity)) return;

        //Check if attribute exists
        var hasManaRend = livingEntity.getAttribute(ASAttributeRegistry.MANA_REND);
        var targetHasMana = victim.getAttribute(AttributeRegistry.MAX_MANA);

        //Cancels modification if user doesn't have mana rend or target doesn't have mana
        if (hasManaRend == null || targetHasMana == null) return;

        //Grab attributes values
        double manaRendAttr = livingEntity.getAttributeValue(ASAttributeRegistry.MANA_REND);
        double victimMaxMana = victim.getAttributeValue(AttributeRegistry.MAX_MANA);
        double victimBaseMana = victim.getAttributeBaseValue(AttributeRegistry.MAX_MANA);

        //Cancels if attributes are 0 to avoid unnecessary calculations
        if (manaRendAttr <= 0 || victimMaxMana <= 0) return;

        //Gets the % of max mana in comparison with base mana (1 = 100%)
        double bonusManaFromBase = (victimMaxMana / victimBaseMana);
        //Bonus damage is 1% for every 100% of mana above base the target has (1% for every 100 extra mana)
        double step = bonusManaFromBase * 0.01;

        //Multiplies step by mana rend, then adds 1 to account for original damage on final multiplication
        double totalExtraDamagerPercent = 1 + (step * manaRendAttr);

        //finalDamage = originalDamage * (1 + step * manaRendAttr)
        event.setAmount((float) (event.getAmount() * totalExtraDamagerPercent));
    }
}
