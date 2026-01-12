package net.acetheeldritchking.aces_spell_utils.events;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.magic.SpellSelectionManager;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.network.SyncManaPacket;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import net.acetheeldritchking.aces_spell_utils.AcesSpellUtils;
import net.acetheeldritchking.aces_spell_utils.items.weapons.MagicGunItem;
import net.acetheeldritchking.aces_spell_utils.registries.ASAttributeRegistry;
import net.acetheeldritchking.aces_spell_utils.utils.ASTags;
import net.acetheeldritchking.aces_spell_utils.utils.ASUtils;
import net.acetheeldritchking.aces_spell_utils.utils.AcesSpellUtilsConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.logging.log4j.core.net.Priority;

@EventBusSubscriber
public class AcesSpellUtilsServerEvents {

    /**
     * MAGIC GUN
     * Code for Magic Gun functionality <p>
     * Casts spell on Magic Gun use
     */
    @SubscribeEvent
    public static void onUseItem(PlayerInteractEvent.RightClickItem event)
    {
        var player = event.getEntity();
        var level = player.level();
        var hand = event.getHand();
        ItemStack itemStack = player.getItemInHand(hand);
        ItemStack mainHand = player.getMainHandItem();

        if (itemStack.getItem() instanceof MagicGunItem gunItem && !gunItem.isHeavyGun())
        {
            SpellSelectionManager spellSelectionManager = new SpellSelectionManager(player);
            SpellSelectionManager.SelectionOption selectionOption = spellSelectionManager.getSelection();
            if (selectionOption == null || selectionOption.spellData.equals(SpellData.EMPTY))
            {
                return;
            }
            SpellData spellData = selectionOption.spellData;
            int spellLevel = spellData.getSpell().getLevelFor(spellData.getLevel(), player);

            if (level.isClientSide())
            {
                if (ClientMagicData.isCasting())
                {
                    event.setCancellationResult(InteractionResult.CONSUME);
                } else if (ClientMagicData.getPlayerMana() < spellData.getSpell().getManaCost(spellLevel)
                        || ClientMagicData.getCooldowns().isOnCooldown(spellData.getSpell())
                        || !ClientMagicData.getSyncedSpellData(player).isSpellLearned(spellData.getSpell()))
                {
                    return;
                } else
                {
                    event.setCancellationResult(InteractionResult.CONSUME);
                }
            }

            var castingSlot = hand.ordinal() == 0 ? SpellSelectionManager.MAINHAND: SpellSelectionManager.OFFHAND;

            if (spellData.getSpell().attemptInitiateCast(itemStack, spellLevel, level, player, selectionOption.getCastSource(), true, castingSlot))
            {
                event.setCancellationResult(InteractionResult.CONSUME);
            } else
            {
                event.setCancellationResult(InteractionResult.FAIL);
            }
            event.setCanceled(true);
        } else if (itemStack.getItem() instanceof MagicGunItem gunItem && (hand.equals(InteractionHand.MAIN_HAND) && gunItem.isHeavyGun()))
        {
            SpellSelectionManager spellSelectionManager = new SpellSelectionManager(player);
            SpellSelectionManager.SelectionOption selectionOption = spellSelectionManager.getSelection();
            if (selectionOption == null || selectionOption.spellData.equals(SpellData.EMPTY))
            {
                return;
            }
            SpellData spellData = selectionOption.spellData;
            int spellLevel = spellData.getSpell().getLevelFor(spellData.getLevel(), player);

            if (level.isClientSide())
            {
                if (ClientMagicData.isCasting())
                {
                    event.setCancellationResult(InteractionResult.CONSUME);
                } else if (ClientMagicData.getPlayerMana() < spellData.getSpell().getManaCost(spellLevel)
                        || ClientMagicData.getCooldowns().isOnCooldown(spellData.getSpell())
                        || !ClientMagicData.getSyncedSpellData(player).isSpellLearned(spellData.getSpell()))
                {
                    return;
                } else
                {
                    event.setCancellationResult(InteractionResult.CONSUME);
                }
            }

            if (spellData.getSpell().attemptInitiateCast(itemStack, spellLevel, level, player, selectionOption.getCastSource(), true, SpellSelectionManager.MAINHAND))
            {
                event.setCancellationResult(InteractionResult.CONSUME);
            } else
            {
                event.setCancellationResult(InteractionResult.FAIL);
            }
            event.setCanceled(true);
        }
    }

    /**
     * MANA STEAL <p>
     * 0 = 0% || 1 = 100% <p>
     * Steals mana based on damage dealt
     */
    @SubscribeEvent
    public static void manaStealEvent(LivingDamageEvent.Post event) {
        var sourceEntity = event.getSource().getEntity();
        var target = event.getEntity();
        var directEntity = event.getSource().getDirectEntity();

        //Safety checks - only works if user is a player
        if (!(sourceEntity instanceof LivingEntity livingEntity)) return;
        if (!(livingEntity instanceof ServerPlayer serverPlayer)) return;
        if (directEntity == null) return;
        if (!((directEntity.getType().is(ASTags.MANA_STEAL_WHITELIST)) || directEntity.is(serverPlayer))) return;

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

        //Check if target is a player for reducing their mana && if the config is enabled
        if (AcesSpellUtilsConfig.manaStealDrain)
        {
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
    }

    /**
     * MANA REND <p>
     * 0 = 0% || 1 = 100% <p>
     * Reduces target's mana based on damage dealt
     */
    @SubscribeEvent
    public static void manaRendEvent(LivingIncomingDamageEvent event) {
        //Grab involved entities
        var victim = event.getEntity();
        var attacker = event.getSource().getEntity();
        var directEntity = event.getSource().getDirectEntity();

        //Cancels modification if user isn't a living entity
        if (!(attacker instanceof LivingEntity livingEntity)) return;
        if (directEntity == null) return;
        if (!((directEntity.getType().is(ASTags.MANA_REND_WHITELIST)) || directEntity.is(attacker))) return;

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

        AcesSpellUtils.LOGGER.debug("Old Damage amount: " + event.getOriginalAmount());
        AcesSpellUtils.LOGGER.debug("New Damage amount: " + event.getAmount());
    }

    /**
     * GOLIATH SLAYER <p>
     * 0 = 0% || 1 = 100% <p>
     * Bonus damage to bosses
     */
    @SubscribeEvent
    public static void goliathSlayerEvent(LivingIncomingDamageEvent event) {
        var victim = event.getEntity();
        var attacker = event.getSource().getEntity();
        if (!(attacker instanceof LivingEntity livingEntity)) return;

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

        AcesSpellUtils.LOGGER.debug("OG Damage: " + baseDamage);
        AcesSpellUtils.LOGGER.debug("Bonus Damage: " + bonusDamage);
        AcesSpellUtils.LOGGER.debug("Total Damage: " + event.getAmount());
    }

    /**
     * HUNGER STEAL <p>
     * 0 = 0% || 1 = 100% <p>
     * Steals hunger based on damage dealt
     */
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

    /**
     * SPELL PENETRATION <p>
     * 0 = 0% || 1 = 100% <p>
     * Ignores magic resistance
     */
    @SubscribeEvent
    public static void spellResPenetrationEvent(LivingIncomingDamageEvent event) {
        var victim = event.getEntity();
        var attacker = event.getSource().getEntity();
        if (!(attacker instanceof LivingEntity livingEntity)) return;

        //Check if attribute exists
        var hasSpellResPen = livingEntity.getAttribute(ASAttributeRegistry.SPELL_RES_PENETRATION);

        //Cancels modification if user doesn't have Spell Res Pen
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

            AcesSpellUtils.LOGGER.debug("OG Damage: " + baseDamage);
            AcesSpellUtils.LOGGER.debug("Bonus Damage: " + bonusDamage);
            AcesSpellUtils.LOGGER.debug("Total Damage: " + event.getAmount());
        }
    }

    /**
     * EVASIVE <p>
     * 0 = 0% || 1 = 100% <p>
     * Dodge chance
     */
    @SubscribeEvent
    public static void evasiveEvent(LivingIncomingDamageEvent event) {
        var victim = event.getEntity();
        var attacker = event.getSource().getEntity();
        if (!(victim instanceof LivingEntity livingEntity)) return;

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

    /**
     * MAGIC CRITICAL <p>
     * 0 = 0% || 1 = 100% <p>
     * Generic magic critical chance and damage <p>
     * Processed after other damage modifiers (lowest priority)
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void magicDamageCriticalStrike(LivingIncomingDamageEvent event)
    {
        var victim = event.getEntity();
        var attacker = event.getSource().getEntity();
        var directEntity = event.getSource().getDirectEntity();

        if (!(attacker instanceof LivingEntity livingEntity)) return;

        //Check if attribute exists
        var hasMagicCritChance = livingEntity.getAttribute(ASAttributeRegistry.MAGIC_DAMAGE_CRIT_CHANCE);
        var hasMagicCritDmg = livingEntity.getAttribute(ASAttributeRegistry.MAGIC_DAMAGE_CRIT_DAMAGE);

        //Cancels modification if user doesn't have attr
        if (hasMagicCritChance == null) return;
        if (hasMagicCritDmg == null) return;

        //Grab attributes value
        double magicCritChance = livingEntity.getAttributeValue(ASAttributeRegistry.MAGIC_DAMAGE_CRIT_CHANCE);
        double magicCritDmg = livingEntity.getAttributeValue(ASAttributeRegistry.MAGIC_DAMAGE_CRIT_DAMAGE);

        // This is for debug
        double baseMagicCritChance = livingEntity.getAttributeValue(ASAttributeRegistry.MAGIC_DAMAGE_CRIT_CHANCE);

        //Cancels if attributes are base to avoid unnecessary calculations
        if (magicCritChance <= 0.05) return;
        if (magicCritDmg <= 1) return;

        // Make sure that the damage source is magic
        if (event.getSource() instanceof SpellDamageSource)
        {
            RandomSource random = victim.getRandom();
            float damage = event.getAmount();

            // I'm looking at how Apothic Attributes does their crit chances/dmg for this
            while (random.nextFloat() <= magicCritChance && magicCritDmg > 1.0F)
            {
                magicCritChance--;
                damage += (float) (event.getAmount() * (magicCritDmg - 1));
                magicCritDmg *= 0.85F;
            }

            if (damage > event.getAmount() && !attacker.level().isClientSide())
            {
                AcesSpellUtils.LOGGER.debug("--CRIT!--");
                attacker.level().playLocalSound(victim.getX(), victim.getY(), victim.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1, 1, false);

                if (attacker instanceof Player player)
                {
                    player.crit(victim);
                } else
                {
                    ASUtils.spawnParticlesInCircle(16, 0.75F, 1.5F, 0.15F, victim, ParticleTypes.CRIT);
                }
            }

            event.setAmount(damage);

            AcesSpellUtils.LOGGER.debug("OG Damage: " + event.getOriginalAmount());
            AcesSpellUtils.LOGGER.debug("Damage: " + damage);
            AcesSpellUtils.LOGGER.debug("Base Chance: " + baseMagicCritChance);
            AcesSpellUtils.LOGGER.debug("Current Chance: " + magicCritChance);
        }
    }

    /**
     * MAGIC PROJECTILE CRITICAL <p>
     * 0 = 0% || 1 = 100% <p>
     * Magic projectile critical chance and damage <p>
     * Processed after other damage modifiers (lowest priority)
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void magicProjectileDamageCriticalStrike(LivingIncomingDamageEvent event)
    {
        var victim = event.getEntity();
        var attacker = event.getSource().getEntity();
        var directEntity = event.getSource().getDirectEntity();

        if (!(attacker instanceof LivingEntity livingEntity)) return;
        if (!(directEntity instanceof AbstractMagicProjectile)) return;

        //Check if attribute exists
        var hasMagicCritChance = livingEntity.getAttribute(ASAttributeRegistry.MAGIC_PROJECTILE_CRIT_CHANCE);
        var hasMagicCritDmg = livingEntity.getAttribute(ASAttributeRegistry.MAGIC_PROJECTILE_CRIT_DAMAGE);

        //Cancels modification if user doesn't have attr
        if (hasMagicCritChance == null) return;
        if (hasMagicCritDmg == null) return;

        //Grab attributes value
        double magicCritChance = livingEntity.getAttributeValue(ASAttributeRegistry.MAGIC_PROJECTILE_CRIT_CHANCE);
        double magicCritDmg = livingEntity.getAttributeValue(ASAttributeRegistry.MAGIC_PROJECTILE_CRIT_DAMAGE);

        // This is for debug
        double baseMagicCritChance = livingEntity.getAttributeValue(ASAttributeRegistry.MAGIC_PROJECTILE_CRIT_CHANCE);

        //Cancels if attributes are base to avoid unnecessary calculations
        if (magicCritChance <= 0.05) return;
        if (magicCritDmg <= 1) return;

        // Make sure that the damage source is magic & we have a projectile
        if (event.getSource() instanceof SpellDamageSource && directEntity instanceof AbstractMagicProjectile)
        {
            RandomSource random = victim.getRandom();
            float damage = event.getAmount();

            // I'm looking at how Apothic Attributes does their crit chances/dmg for this
            while (random.nextFloat() <= magicCritChance && magicCritDmg > 1.0F)
            {
                magicCritChance--;
                damage += (float) (event.getAmount() * (magicCritDmg - 1));
                magicCritDmg *= 0.85F;
            }

            if (damage > event.getAmount() && !attacker.level().isClientSide())
            {
                AcesSpellUtils.LOGGER.debug("--PROJ CRIT!--");
                attacker.level().playLocalSound(victim.getX(), victim.getY(), victim.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1, 1, false);

                if (attacker instanceof Player player)
                {
                    player.crit(victim);
                } else
                {
                    ASUtils.spawnParticlesInCircle(16, 0.75F, 1.5F, 0.15F, victim, ParticleTypes.CRIT);
                }
            }

            event.setAmount(damage);

            AcesSpellUtils.LOGGER.debug("OG Damage: " + event.getOriginalAmount());
            AcesSpellUtils.LOGGER.debug("Damage: " + damage);
            AcesSpellUtils.LOGGER.debug("Base Chance: " + baseMagicCritChance);
            AcesSpellUtils.LOGGER.debug("Current Chance: " + magicCritChance);
        }
    }

    /*
     * Removed the code from here because it didn't make sense
     * They can already be combined, and they multiply each other
     */

    /**
     * MAGIC PROJECTILE BONUS DAMAGE <p>
     * 0 = 0% || 1 = 100% <p>
     * Bonus magic projectile damage <p>
     * Processed after most damage modifiers (low priority)
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void magicProjectileBonusDamage(LivingIncomingDamageEvent event) {
        var victim = event.getEntity();
        var attacker = event.getSource().getEntity();
        var directEntity = event.getSource().getDirectEntity();
        if (!(attacker instanceof LivingEntity livingEntity)) return;
        if (!(directEntity instanceof AbstractMagicProjectile projectile)) return;

        //Check if attribute exists
        var hasMagicProjDmg = livingEntity.getAttribute(ASAttributeRegistry.MAGIC_PROJECTILE_DAMAGE);

        //Cancels modification if user doesn't have attr
        if (hasMagicProjDmg == null) return;

        //Grab attributes value
        double magicProjDmg = 1 + livingEntity.getAttributeValue(ASAttributeRegistry.MAGIC_PROJECTILE_DAMAGE);

        //Cancels if attributes are 0 to avoid unnecessary calculations
        if (magicProjDmg <= 0) return;

        if (event.getSource() instanceof SpellDamageSource && directEntity instanceof AbstractMagicProjectile)
        {
            float baseDamage = event.getOriginalAmount();
            float totalDamage = (float)(baseDamage * magicProjDmg);

            event.setAmount(totalDamage);

            AcesSpellUtils.LOGGER.debug("OG Proj Damage: " + baseDamage);
            AcesSpellUtils.LOGGER.debug("Bonus Proj Damage: " + (baseDamage * (magicProjDmg - 1)));
            AcesSpellUtils.LOGGER.debug("Total Proj Damage: " + event.getAmount());
        }
    }

    /**
     * LIFE RECOVERY <p>
     * 0 = 0% || 1 = 100% <p>
     * Recovers lost health on-hit
     */
    @SubscribeEvent
    public static void lifeRecovery(LivingDamageEvent.Post event) {
        var victim = event.getEntity();
        var attacker = event.getSource().getEntity();
        if (!(attacker instanceof LivingEntity livingEntity)) return;

        //Check if attribute exists
        var hasLifeRecovery = livingEntity.getAttribute(ASAttributeRegistry.LIFE_RECOVERY);

        //Cancels modification if user doesn't have Goliath Slayer
        if (hasLifeRecovery == null) return;

        //Grab attributes value
        double lifeRecoveryAttr = livingEntity.getAttributeValue(ASAttributeRegistry.LIFE_RECOVERY);

        //Cancels if attributes are 0 to avoid unnecessary calculations
        if (lifeRecoveryAttr <= 0) return;

        //Getting the missing health (maximum - current) instead of just maximum
        final float MAX_HEALTH = livingEntity.getMaxHealth();
        //1.0 recovery = recovers the entire missing health
        float recoveryAmount = (float) (MAX_HEALTH * lifeRecoveryAttr);

        livingEntity.heal(recoveryAmount);

        AcesSpellUtils.LOGGER.debug("HP: " + livingEntity.getHealth());
        AcesSpellUtils.LOGGER.debug("Healed for: " + recoveryAmount);
    }
}
