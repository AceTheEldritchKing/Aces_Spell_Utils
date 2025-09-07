package net.acetheeldritchking.aces_spell_utils.events;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.network.SyncManaPacket;
import net.acetheeldritchking.aces_spell_utils.AcesSpellUtils;
import net.acetheeldritchking.aces_spell_utils.registries.ASAttributeRegistry;
import net.acetheeldritchking.aces_spell_utils.utils.ASTags;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber
public class AcesSpellUtilsServerEvents {
    /***
     * ATTRIBUTES
     */
    @SubscribeEvent
    public static void manaStealEvent(LivingDamageEvent.Post event) {
        var sourceEntity = event.getSource().getEntity();
        var target = event.getEntity();
        var projectile = event.getSource().getDirectEntity();

        //Safety checks - only works if user is a player
        if (!(sourceEntity instanceof LivingEntity livingEntity)) return;
        if (!(livingEntity instanceof ServerPlayer serverPlayer)) return;

        var hasManaSteal = serverPlayer.getAttribute(ASAttributeRegistry.MANA_STEAL);

        /***
         * Mana Steal Attribute
         */
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

        /***
         * Mana Rend Attribute
         */
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

    // You can and should use one event for each attribute
    // Just make sure they always end early if not used, to avoid random stuff firing
    @SubscribeEvent
    public static void goliathSlayerEvent(LivingIncomingDamageEvent event) {
        var victim = event.getEntity();
        var attacker = event.getSource().getEntity();
        if (!(attacker instanceof LivingEntity livingEntity)) return;
        /***
         * Goliath Slayer Attribute
         */
        //Check if attribute exists
        var hasGoliathSlayer = livingEntity.getAttribute(ASAttributeRegistry.GOLIATH_SLAYER);

        //Cancels modification if user doesn't have Goliath Slayer
        if (hasGoliathSlayer == null) return;

        //Grab attributes value
        double goliathSlayerAttr = livingEntity.getAttributeValue(ASAttributeRegistry.GOLIATH_SLAYER);

        //Cancels if attributes are 0 to avoid unnecessary calculations
        if (goliathSlayerAttr <= 0) return;

        // Eval whether the victim is a boss entity
        // It doesn't do anything on non-boss, so we can just return otherwise
        if (!victim.getType().is(ASTags.BOSS_LIKE_ENTITES)) return;
        // Really, it's just a percentage of damage, nothing complicated
        float baseDamage = event.getOriginalAmount();
        float bonusDamage = (float) (baseDamage * goliathSlayerAttr);
        float totalDamage = baseDamage + bonusDamage;

        event.setAmount(totalDamage);

        //System.out.println("OG Damage: " + baseDamage);
        //System.out.println("Bonus Damage: " + bonusDamage);
        //System.out.println("Total Damage: " + event.getAmount());
    }

    // Hunger Steal (0 = 0% || 1 = 100%)
    @SubscribeEvent
    public static void hungerStealEvent(LivingDamageEvent.Pre event)
    {
        var sourceEntity = event.getSource().getEntity();
        var target = event.getEntity();

        //Safety checks - only works if user is a player
        if (!(sourceEntity instanceof LivingEntity livingEntity)) return;
        if (!(livingEntity instanceof ServerPlayer serverPlayer)) return;

        var hasHungerSteal = serverPlayer.getAttribute(ASAttributeRegistry.HUNGER_STEAL);

        //Check if user has hunger steal
        if (hasHungerSteal == null) return;

        double hungerStealAttr = serverPlayer.getAttributeValue(ASAttributeRegistry.HUNGER_STEAL);

        //Cancels if attributes are 0 to avoid unnecessary calculations
        if (hungerStealAttr <= 0) return;

        // I took most of this from Art of Forging
        FoodData playerFood = serverPlayer.getFoodData();
        int foodLevel = playerFood.getFoodLevel();

        int addFood = (int) Math.max((hungerStealAttr) + foodLevel, foodLevel);

        playerFood.setFoodLevel(addFood);

        if (target instanceof Player targetPlayer) {
            FoodData targetFood = targetPlayer.getFoodData();
            int targetFoodLevel = playerFood.getFoodLevel();

            int subFood = (int) Math.min((hungerStealAttr) - targetFoodLevel, targetFoodLevel);

            // This should reduce hunger, hopefully
            targetFood.setFoodLevel(subFood);
        }
    }

    // Spell Res Pen (0 = 0% || 1 = 100%)
    @SubscribeEvent
    public static void spellResPenetrationEvent(LivingIncomingDamageEvent event) {
        var victim = event.getEntity();
        var attacker = event.getSource().getEntity();
        if (!(attacker instanceof LivingEntity livingEntity)) return;
        /***
         * Spell Res Pen Attribute
         */
        //Check if attribute exists
        var hasSpellResPen = livingEntity.getAttribute(ASAttributeRegistry.SPELL_RES_PENETRATION);

        //Cancels modification if user doesn't have Goliath Slayer
        if (hasSpellResPen == null) return;

        //Grab attributes value
        double spellResPenAttr = livingEntity.getAttributeValue(ASAttributeRegistry.SPELL_RES_PENETRATION);
        double spellResAttr = victim.getAttributeValue(AttributeRegistry.SPELL_RESIST);

        //Cancels if attributes are 0 to avoid unnecessary calculations
        if (spellResPenAttr <= 0) return;

        // Make sure the source is from magic
        if (event.getSource() instanceof SpellDamageSource)
        {
            float baseDamage = event.getOriginalAmount();
            // Take the spell res attribute of the victim, then add it to the penetration value to get the bonus
            float bonusDamage = (float) (baseDamage * (spellResPenAttr + spellResAttr));
            float totalDamage = baseDamage + bonusDamage;

            event.setAmount(totalDamage);

            //System.out.println("OG Damage: " + baseDamage);
            //System.out.println("Bonus Damage: " + bonusDamage);
            //System.out.println("Total Damage: " + event.getAmount());
        }
    }

    @SubscribeEvent
    public static void evasiveEvent(LivingIncomingDamageEvent event) {
        var victim = event.getEntity();
        var attacker = event.getSource().getEntity();
        if (!(victim instanceof LivingEntity livingEntity)) return;
        /***
         * Evasive Attribute
         */
        //Check if attribute exists
        var hasEvasive = livingEntity.getAttribute(ASAttributeRegistry.EVASIVE);

        //Cancels modification if user doesn't have Goliath Slayer
        if (hasEvasive == null) return;

        //Grab attributes value
        double evasiveAttr = livingEntity.getAttributeValue(ASAttributeRegistry.EVASIVE);

        //Cancels if attributes are 0 to avoid unnecessary calculations
        if (evasiveAttr <= 0) return;

        // Increasing Invul time
        int postInvulTicks = event.getContainer().getPostAttackInvulnerabilityTicks();
        postInvulTicks *= (int) evasiveAttr;

        event.setInvulnerabilityTicks(postInvulTicks);

        AcesSpellUtils.LOGGER.debug("I Frames: " + livingEntity.invulnerableTime);
        if (!livingEntity.level().isClientSide())
        {
            MagicManager.spawnParticles(livingEntity.level(), ParticleTypes.SMOKE,
                    livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(),
                    25, 0.4, 0.8, 0.4, 0.03, false);
        }
    }
}
