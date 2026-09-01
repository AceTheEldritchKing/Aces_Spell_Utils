package net.acetheeldritchking.aces_spell_utils.events;

import io.redspace.ironsspellbooks.api.events.SpellDamageEvent;
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
import net.acetheeldritchking.aces_spell_utils.entity.mobs.IKeepInventoryEntity;
import net.acetheeldritchking.aces_spell_utils.items.weapons.MagicGunItem;
import net.acetheeldritchking.aces_spell_utils.registries.ASAttributeRegistry;
import net.acetheeldritchking.aces_spell_utils.utils.ASTags;
import net.acetheeldritchking.aces_spell_utils.utils.ASUtils;
import net.acetheeldritchking.aces_spell_utils.utils.AcesSpellUtilsConfig;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.logging.log4j.core.net.Priority;

import java.util.ArrayList;
import java.util.List;

import static io.redspace.ironsspellbooks.damage.DamageSources.getResist;
import net.acetheeldritchking.aces_spell_utils.trail.TrailManager;
import net.acetheeldritchking.aces_spell_utils.utils.RibbonHandler;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

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
     * https://www.desmos.com/calculator/9sxfmzwq4v
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
        // Config
        if (AcesSpellUtilsConfig.manaStealWhitelist)
        {
            if (!((directEntity.getType().is(ASTags.MANA_STEAL_WHITELIST)) || directEntity.is(serverPlayer))) return;
        }
        //Check if user has mana steal
        var hasManaSteal = serverPlayer.getAttribute(ASAttributeRegistry.MANA_STEAL);
        if (hasManaSteal == null) return;

        float manaStealAttr = (float) serverPlayer.getAttributeValue(ASAttributeRegistry.MANA_STEAL);
        //Cancels if Attribute is 0 to avoid unnecessary calculations
        if (manaStealAttr <= 0) return;

        var attackerMagicData = MagicData.getPlayerMagicData(serverPlayer);
        int attackerMaxMana = (int) serverPlayer.getAttributeValue(AttributeRegistry.MAX_MANA);
        int attackerOriginalMana = (int) attackerMagicData.getMana();

        int potentialStolenMana = (int) (manaStealAttr * event.getOriginalDamage());

        //Check if target is a player for reducing their mana && if the config is enabled
        if (target instanceof ServerPlayer victim && AcesSpellUtilsConfig.manaStealDrain == true)
        {
            var victimMagicData = MagicData.getPlayerMagicData(victim);
            int victimMaxMana = (int) victim.getAttributeValue(AttributeRegistry.MAX_MANA);
            int victimOriginalMana = (int) victimMagicData.getMana();
            //Final check for applying Mana Steal
            if (victimMaxMana <= 0) return;

            //Calculate how much mana is stolen
            int stolenMana= Math.min(potentialStolenMana, victimOriginalMana);

            //Remove stolen mana from victim
            int victimFinalMana = victimOriginalMana - stolenMana;
            victimMagicData.setMana(victimFinalMana);
            PacketDistributor.sendToPlayer(serverPlayer, new SyncManaPacket(victimMagicData));

            //Add stolen mana to Attacker
            int attackerFinalMana = Math.min(attackerOriginalMana + stolenMana, attackerMaxMana);
            attackerMagicData.setMana(attackerFinalMana);
            PacketDistributor.sendToPlayer(serverPlayer, new SyncManaPacket(attackerMagicData));

            if (AcesSpellUtilsConfig.devMode == true)
            {
                AcesSpellUtils.LOGGER.debug("Potential Stolen Mana: " + potentialStolenMana);
                AcesSpellUtils.LOGGER.debug("Victim Original Mana: " + victimOriginalMana);
                AcesSpellUtils.LOGGER.debug("Attacker Max Mana: " + attackerMaxMana);
                AcesSpellUtils.LOGGER.debug("Mana Stolen: " + stolenMana);
            }
        } else {
            //Add "Stolen" mana to Attacker
            int attackerFinalMana = Math.min(attackerOriginalMana + potentialStolenMana, attackerMaxMana);
            attackerMagicData.setMana(attackerFinalMana);
            PacketDistributor.sendToPlayer(serverPlayer, new SyncManaPacket(attackerMagicData));

            if (AcesSpellUtilsConfig.devMode == true)
            {
                AcesSpellUtils.LOGGER.debug("Mana Gained: " + potentialStolenMana);
            }
        }
    }

    /**
     * MANA REND <p>
     * 0 = 0% || 1 = 100% <p>
     * Increases Damage dealt based on the target's Max Mana
     * https://www.desmos.com/calculator/f60u9h02mq
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
        // Config
        if (AcesSpellUtilsConfig.manaRendWhitelist)
        {
            if (!((directEntity.getType().is(ASTags.MANA_REND_WHITELIST)) || directEntity.is(attacker))) return;
        }

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

        //Gets the amount of max mana above base mana (100 base)
        var manaAboveBase = victimMaxMana - victimBaseMana;

        //Note: Adds 1 to account for original damage on final multiplication
        double totalExtraDamagerPercent = 1 + manaRendAttr * manaAboveBase/1000;

        //finalDamage = originalDamage * (1 + manaRendAttr * manaAboveBase/1000)
        event.setAmount((float) (event.getAmount() * totalExtraDamagerPercent));

        if (AcesSpellUtilsConfig.devMode == true)
        {
            AcesSpellUtils.LOGGER.debug("MANA REND Old Damage amount: " + event.getOriginalAmount());
            AcesSpellUtils.LOGGER.debug("MANA REND New Damage amount: " + event.getAmount());
        }
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

        if (AcesSpellUtilsConfig.devMode == true)
        {
            AcesSpellUtils.LOGGER.debug("GOLIATH SLAYER OG Damage: " + baseDamage);
            AcesSpellUtils.LOGGER.debug("GOLIATH SLAYER Bonus Damage: " + bonusDamage);
            AcesSpellUtils.LOGGER.debug("GOLIATH SLAYER Total Damage: " + event.getAmount());
        }
    }

    /**
     * HUNGER STEAL <p>
     * 1 = 1 hunger point stolen <p>
     * Steals hunger points on Melee hit
     */
    @SubscribeEvent
    public static void hungerStealEvent(LivingDamageEvent.Pre event)
    {
        var directEntity = event.getSource().getDirectEntity();
        var target = event.getEntity();

        //Safety checks
        //Check that the damage source was a Melee attack
        //Check that the user is a Player
        if (!(directEntity instanceof LivingEntity livingEntity)) return;
        if (!(livingEntity instanceof ServerPlayer serverPlayer)) return;

        var hasHungerSteal = serverPlayer.getAttribute(ASAttributeRegistry.HUNGER_STEAL);

        //Check if user has hunger steal
        if (hasHungerSteal == null) return;

        //Check if attack was made at full charge
        float weaponCharge = serverPlayer.getAttackStrengthScale(0);
        if (weaponCharge < 1) return;

        double hungerStealAttr = serverPlayer.getAttributeValue(ASAttributeRegistry.HUNGER_STEAL);
        //Cancels if attributes are 0 to avoid unnecessary calculations
        if (hungerStealAttr <= 0) return;

        // I took most of this from Art of Forging
        FoodData playerFood = serverPlayer.getFoodData();
        int foodLevel = playerFood.getFoodLevel();

        //
        int addFood = (int) Math.clamp(foodLevel + hungerStealAttr, 0, 20);

        playerFood.setFoodLevel(addFood);

        if (target instanceof Player targetPlayer) {
            FoodData targetFood = targetPlayer.getFoodData();
            int targetFoodLevel = playerFood.getFoodLevel();

            int subFood = (int) Math.clamp(targetFoodLevel - hungerStealAttr, 0, 20);

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
    public static void spellResPenetrationEvent(SpellDamageEvent event) {
        var victim = event.getEntity();
        var attacker = event.getSpellDamageSource().getEntity();
        if (!(attacker instanceof LivingEntity livingEntity)) return;

        //Check if attribute exists
        var hasSpellResPen = livingEntity.getAttribute(ASAttributeRegistry.SPELL_RES_PENETRATION);
        var hasSpellRes = victim.getAttribute(AttributeRegistry.SPELL_RESIST);

        //Cancels modification if user doesn't have Spell Res Pen or Spell Res
        if (hasSpellResPen == null) return;
        if (hasSpellRes == null) return;

        //Grab attributes value
        double spellResPenAttr = livingEntity.getAttributeValue(ASAttributeRegistry.SPELL_RES_PENETRATION);
        double spellResAttr = hasSpellRes.getValue();

        //Cancels if attributes are 0 to avoid unnecessary calculations
        if (spellResPenAttr <= 0) return;
        if (spellResAttr <= 1) return;

        // Make sure the source is from magic
        if (event.getSpellDamageSource() instanceof SpellDamageSource spellDamage)
        {
            float baseDamage = event.getOriginalAmount();
            float baseResist = getResist(victim, spellDamage.spell().getSchoolType());
            float softcappedResist = 2 - baseResist;
            float penModifier = (float) (1 + spellResPenAttr);
            // If Spell Res Pen attribute +1 is greater than the Softcapped Spell Res attribute, then return a modifier of 1 (100% penetration)
            // otherwise divide the Softcapped Spell Res attribute by the Spell Res Pen attribute +1 to get a new Modifier to apply to the Base Damage to get the Final Damage
            // https://www.desmos.com/calculator/i4zjbstnls
            float damageModifier = (2 - (penModifier > softcappedResist ? 1 : softcappedResist / penModifier));
            // Apply Damage Modifier to Base damage and divide by Base Resist to account for ISS multiplying the Adjusted Damage by Base Resist again
            float adjustedDamage = baseDamage * damageModifier / baseResist;
            // Note: This applies before baseResist is multiplied with it by ISS
            event.setAmount(adjustedDamage);
            // Final Damage = baseDamage * damageModifier

            if (AcesSpellUtilsConfig.devMode == true)
            {
                AcesSpellUtils.LOGGER.debug("SPELL RES ATTR: " + spellResAttr);
                AcesSpellUtils.LOGGER.debug("SPELL RES PEN ATTR: " + spellResPenAttr);
                AcesSpellUtils.LOGGER.debug("SPELL RES PEN OG Damage: " + baseDamage);
                AcesSpellUtils.LOGGER.debug("SPELL RES PEN OG Modifier: " + baseResist);
                AcesSpellUtils.LOGGER.debug("SPELL RES PEN Adjusted Modifier: " + damageModifier);
                AcesSpellUtils.LOGGER.debug("SPELL RES PEN Adjusted Damage: " + event.getAmount());
                AcesSpellUtils.LOGGER.debug("SPELL RES PEN Final Damage: " + (adjustedDamage * baseResist));
            }
        }
    }

    /**
     * EVASIVE <p>
     * 0 = 0% || 1 = 100% <p>
     * Increases invulnerability frames
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

        if (AcesSpellUtilsConfig.devMode == true)
        {
            AcesSpellUtils.LOGGER.debug("I Frames: " + livingEntity.invulnerableTime);
        }
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
                if (AcesSpellUtilsConfig.devMode == true)
                {
                    AcesSpellUtils.LOGGER.debug("--CRIT!--");
                }
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

            if (AcesSpellUtilsConfig.devMode == true)
            {
                AcesSpellUtils.LOGGER.debug("MAGIC CRIT OG Damage: " + event.getOriginalAmount());
                AcesSpellUtils.LOGGER.debug("MAGIC CRIT Damage: " + damage);
                AcesSpellUtils.LOGGER.debug("MAGIC CRIT Base Chance: " + baseMagicCritChance);
                AcesSpellUtils.LOGGER.debug("MAGIC CRIT Current Chance: " + magicCritChance);
            }
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
                if (AcesSpellUtilsConfig.devMode == true)
                {
                    AcesSpellUtils.LOGGER.debug("--PROJ CRIT!--");
                }
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

            if (AcesSpellUtilsConfig.devMode == true)
            {
                AcesSpellUtils.LOGGER.debug("MAGIC PROJ CRIT OG Damage: " + event.getOriginalAmount());
                AcesSpellUtils.LOGGER.debug("MAGIC PROJ CRIT Damage: " + damage);
                AcesSpellUtils.LOGGER.debug("MAGIC PROJ CRIT Base Chance: " + baseMagicCritChance);
                AcesSpellUtils.LOGGER.debug("MAGIC PROJ CRIT Current Chance: " + magicCritChance);
            }
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
        if (magicProjDmg <= 1) return;

        if (event.getSource() instanceof SpellDamageSource)
        {
            float baseDamage = event.getAmount();
            float totalDamage = (float)(baseDamage * magicProjDmg);

            event.setAmount(totalDamage);

            if (AcesSpellUtilsConfig.devMode == true)
            {
                AcesSpellUtils.LOGGER.debug("MAGIC PROJ OG Damage: " + baseDamage);
                AcesSpellUtils.LOGGER.debug("MAGIC PROJ Bonus Damage: " + (baseDamage * (magicProjDmg - 1)));
                AcesSpellUtils.LOGGER.debug("MAGIC PROJ Total Damage: " + event.getAmount());
            }
        }
    }

    /**
     * LIFE RECOVERY <p>
     * 0 = 0% || 1 = 100% <p>
     * Recovers a % of max health on-hit
     */
    @SubscribeEvent
    public static void lifeRecovery(LivingDamageEvent.Post event) {
        // TO DO: Make this configurable to either melee/spell/both
        var attacker = event.getSource().getEntity();
        if (!(attacker instanceof LivingEntity livingEntity)) return;

        //Check if attribute exists
        var hasLifeRecovery = livingEntity.getAttribute(ASAttributeRegistry.LIFE_RECOVERY);

        //Cancels modification if user doesn't have Life Recovery
        if (hasLifeRecovery == null) return;

        //If the entity is a Player, Check if attack was made at full charge
        if (livingEntity instanceof ServerPlayer serverPlayer) {
            float weaponCharge = serverPlayer.getAttackStrengthScale(0);
            if (weaponCharge < 1) return;
        }

        //Grab attributes value
        double lifeRecoveryAttr = livingEntity.getAttributeValue(ASAttributeRegistry.LIFE_RECOVERY);

        //Cancels if attributes are 0 to avoid unnecessary calculations
        if (lifeRecoveryAttr <= 0) return;

        final float MAX_HEALTH = livingEntity.getMaxHealth();
        //1.0 recovery = fully restores health
        float recoveryAmount = (float) (MAX_HEALTH * lifeRecoveryAttr);

        livingEntity.heal(recoveryAmount);

        if (AcesSpellUtilsConfig.devMode == true)
        {
            AcesSpellUtils.LOGGER.debug("LIFE RECOVERY: HP: " + livingEntity.getHealth());
            AcesSpellUtils.LOGGER.debug("LIFE RECOVERY: Healed for: " + recoveryAmount);
        }
    }

    /**
     * VIGOR REAP <p>
     * 0 = 0% || 1 = 100% <p>
     * Recovers a % of missing health on Melee hit
     */
    @SubscribeEvent
    public static void vigorReap(LivingDamageEvent.Post event) {
        // TO DO: Make this configurable to either melee/spell/both
        //Check that the damage source was a Melee attack
        var directEntity = event.getSource().getDirectEntity();
        if (!(directEntity instanceof LivingEntity livingEntity)) return;

        //Check if attribute exists
        var hasDetermination = livingEntity.getAttribute(ASAttributeRegistry.VIGOR_REAP);

        //Cancels modification if user doesn't have Life Recovery
        if (hasDetermination == null) return;

        //If the entity is a Player, Check if attack was made at full charge
        if (livingEntity instanceof ServerPlayer serverPlayer) {
            float weaponCharge = serverPlayer.getAttackStrengthScale(0);
            if (weaponCharge < 1) return;
        }

        //Grab attributes value
        double determinationAttr = livingEntity.getAttributeValue(ASAttributeRegistry.VIGOR_REAP);

        //Cancels if attributes are 0 to avoid unnecessary calculations
        if (determinationAttr <= 0) return;

        //Getting the missing health (maximum - current) instead of just maximum
        final float MAX_HEALTH = livingEntity.getMaxHealth();
        final float CURRENT_HEALTH = livingEntity.getHealth();
        float MISSING_HEALTH = MAX_HEALTH - CURRENT_HEALTH;
        //1.0 recovery = recovers the entire missing health
        float recoveryAmount = (float) (MISSING_HEALTH * determinationAttr);

        livingEntity.heal(recoveryAmount);

        if (AcesSpellUtilsConfig.devMode == true)
        {
            AcesSpellUtils.LOGGER.debug("VIGOR REAP: HP: " + livingEntity.getHealth());
            AcesSpellUtils.LOGGER.debug("VIGOR REAP: Healed for: " + recoveryAmount);
        }
    }

    @SubscribeEvent
    public static void keepInvEntityPlayerDeathEvent(LivingDeathEvent event)
    {
        var entity = event.getEntity();
        Level level = entity.level();

        if (!(entity instanceof ServerPlayer serverPlayer)) return;
        if (level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) return;

        // Detect if the player is in the radius of the killer
        List<LivingEntity> entitiesNearby = entity.level().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(IKeepInventoryEntity.range * 2));
        for (LivingEntity livingEntity : entitiesNearby)
        {
            if (livingEntity instanceof IKeepInventoryEntity keepInventoryEntity)
            {
                CompoundTag inv = saveInventory(serverPlayer.getInventory());
                serverPlayer.getPersistentData().putBoolean(IKeepInventoryEntity.WAS_KILLED_BY_ENTITY, true);
                serverPlayer.getPersistentData().put(IKeepInventoryEntity.KEEP_INV, inv);

                serverPlayer.restoreFrom(serverPlayer, true);
            }
        }
    }

    @SubscribeEvent
    public static void keepInvPreventDrops(LivingDropsEvent event)
    {
        var entity = event.getEntity();
        if (!(entity instanceof ServerPlayer serverPlayer)) return;
        if (serverPlayer.getPersistentData().getBoolean(IKeepInventoryEntity.WAS_KILLED_BY_ENTITY))
        {
            event.getDrops().clear();
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event)
    {
        if (!event.isWasDeath()) return;

        Player ogPlayer = event.getOriginal();
        Player newPlayer = event.getEntity();

        CompoundTag persistentData = ogPlayer.getPersistentData();
        if (!persistentData.getBoolean(IKeepInventoryEntity.WAS_KILLED_BY_ENTITY)) return;

        if (persistentData.contains(IKeepInventoryEntity.KEEP_INV, Tag.TAG_COMPOUND))
        {
            CompoundTag inv = persistentData.getCompound(IKeepInventoryEntity.KEEP_INV);
            restoreInventory(newPlayer.getInventory(), inv);
        }

        newPlayer.getPersistentData().remove(IKeepInventoryEntity.WAS_KILLED_BY_ENTITY);
        newPlayer.getPersistentData().remove(IKeepInventoryEntity.KEEP_INV);
    }

    @SubscribeEvent
    public static void onPlayerRespawnEvent(PlayerEvent.PlayerRespawnEvent event)
    {
        var entity = event.getEntity();

        if (!(entity instanceof ServerPlayer serverPlayer)) return;

        CompoundTag persistentData = serverPlayer.getPersistentData();

        if (!persistentData.getBoolean(IKeepInventoryEntity.WAS_KILLED_BY_ENTITY)) return;

        if (persistentData.contains(IKeepInventoryEntity.KEEP_INV, Tag.TAG_COMPOUND))
        {
            CompoundTag inv = persistentData.getCompound(IKeepInventoryEntity.KEEP_INV);
            restoreInventory(serverPlayer.getInventory(), inv);
        }
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event)
    {
        TrailManager.tick();
        RibbonHandler.pruneDead();
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event)
    {
        TrailManager.clear();
        RibbonHandler.clear();
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event)
    {
        if (event.getEntity() instanceof ServerPlayer serverPlayer)
        {
            RibbonHandler.resend(serverPlayer, event.getTarget());
        }
    }
    private static CompoundTag saveInventory(Inventory inv) {
        CompoundTag root = new CompoundTag();

        ListTag items = new ListTag();
        for (int i = 0; i < inv.items.size(); i++) {
            ItemStack st = inv.items.get(i);
            CompoundTag t = new CompoundTag();
            t.putByte("Slot", (byte) i);
            if (!st.isEmpty()) st.save(inv.player.registryAccess(), t);
            items.add(t);
        }
        root.put("Items", items);

        ListTag armor = new ListTag();
        for (int i = 0; i < inv.armor.size(); i++) {
            ItemStack st = inv.armor.get(i);
            CompoundTag t = new CompoundTag();
            t.putByte("Slot", (byte) i);
            if (!st.isEmpty()) st.save(inv.player.registryAccess(), t);
            armor.add(t);
        }
        root.put("Armor", armor);

        ListTag offhand = new ListTag();
        for (int i = 0; i < inv.offhand.size(); i++) {
            ItemStack st = inv.offhand.get(i);
            CompoundTag t = new CompoundTag();
            t.putByte("Slot", (byte) i);
            if (!st.isEmpty()) st.save(inv.player.registryAccess(), t);
            offhand.add(t);
        }
        root.put("Offhand", offhand);

        return root;
    }

    private static void restoreInventory(Inventory inv, CompoundTag root) {
        inv.items.replaceAll(ignored -> ItemStack.EMPTY);
        inv.armor.replaceAll(ignored -> ItemStack.EMPTY);
        inv.offhand.replaceAll(ignored -> ItemStack.EMPTY);

        ListTag items = root.getList("Items", Tag.TAG_COMPOUND);
        for (int i = 0; i < items.size(); i++) {
            CompoundTag t = items.getCompound(i);
            int slot = t.getByte("Slot") & 255;
            ItemStack st = ItemStack.parseOptional(inv.player.registryAccess(), t);
            if (slot >= 0 && slot < inv.items.size()) inv.items.set(slot, st);
        }

        ListTag armor = root.getList("Armor", Tag.TAG_COMPOUND);
        for (int i = 0; i < armor.size(); i++) {
            CompoundTag t = armor.getCompound(i);
            int slot = t.getByte("Slot") & 255;
            ItemStack st = ItemStack.parseOptional(inv.player.registryAccess(), t);
            if (slot >= 0 && slot < inv.armor.size()) inv.armor.set(slot, st);
        }

        ListTag offhand = root.getList("Offhand", Tag.TAG_COMPOUND);
        for (int i = 0; i < offhand.size(); i++) {
            CompoundTag t = offhand.getCompound(i);
            int slot = t.getByte("Slot") & 255;
            ItemStack st = ItemStack.parseOptional(inv.player.registryAccess(), t);
            if (slot >= 0 && slot < inv.offhand.size()) inv.offhand.set(slot, st);
        }
    }

//    @SubscribeEvent
//    public static void addPlayersToKeepInvListEvent(EntityJoinLevelEvent event)
//    {
//        if (!(event.getLevel() instanceof ServerLevel serverLevel) || event.loadedFromDisk()) return;
//
//        var entity = event.getEntity();
//
//        if (entity instanceof IKeepInventoryEntity keepInventoryEntity)
//        {
//            AcesSpellUtils.LOGGER.debug("Is it a keep inv entity (join)?");
//            double rangeSqr = keepInventoryEntity.keepInventoryDetectionRange();
//            rangeSqr *= rangeSqr;
//            Vec3 center = entity.position();
//            List<ServerPlayer> keepInvPlayers = new ArrayList<>();
//            for (ServerPlayer player : serverLevel.players())
//            {
//                if (player.isCreative() || player.isSpectator() || player.distanceToSqr(center) > rangeSqr) {
//                    continue;
//                }
//                keepInvPlayers.add(player);
//                keepInventoryEntity.setParticipantsFromServerPlayers(keepInvPlayers);
//
//                for (int i = 0; i < keepInvPlayers.size(); i++)
//                {
//                    AcesSpellUtils.LOGGER.debug("participants (list event): " + keepInvPlayers.get(i));
//                }
//            }
//        }
//    }
//
//    @SubscribeEvent
//    public static void keepInvPlayerListRestoreEvent(PlayerEvent.Clone event)
//    {
//        var oldEntity = event.getOriginal();
//        var entity = event.getEntity();
//        var killer = oldEntity.getLastAttacker();
//
//        if (!(entity.level() instanceof ServerLevel serverLevel)) return;
//
//        if (killer instanceof IKeepInventoryEntity keepInventoryEntity && event.isWasDeath())
//        {
//            AcesSpellUtils.LOGGER.debug("Is it a keep inv entity?");
//            if (oldEntity instanceof ServerPlayer oldPlayer && entity instanceof ServerPlayer newPlayer)
//            {
//                ServerPlayer participant = keepInventoryEntity.getParticipantsFromServer(serverLevel);
//                AcesSpellUtils.LOGGER.debug("participants: " + participant);
//                if (oldPlayer.is(participant))
//                {
//                    AcesSpellUtils.LOGGER.debug("Do we even go here?");
//                    newPlayer.getInventory().replaceWith(oldPlayer.getInventory());
//                    newPlayer.experienceLevel = oldPlayer.experienceLevel;
//                    newPlayer.totalExperience = oldPlayer.totalExperience;
//                    newPlayer.experienceProgress = oldPlayer.experienceProgress;
//                    newPlayer.setScore(oldPlayer.getScore());
//                }
//            }
//            /*double rangeSqr = keepInventoryEntity.keepInventoryDetectionRange();
//            rangeSqr *= rangeSqr;
//            Vec3 center = killer.position();
//            for (ServerPlayer player : serverLevel.players())
//            {
//                if (player.isCreative() || player.isSpectator() || player.distanceToSqr(center) > rangeSqr) {
//                    continue;
//                }
//                ServerPlayer participant = keepInventoryEntity.getParticipantsFromServer(serverLevel);
//
//                if (player.is(participant))
//                {
//                    player.getInventory().replaceWith(participant.getInventory());
//                    player.experienceLevel = participant.experienceLevel;
//                    player.totalExperience = participant.totalExperience;
//                    player.experienceProgress = participant.experienceProgress;
//                    player.setScore(participant.getScore());
//                }
//            }*/
//        }
//    }
}
