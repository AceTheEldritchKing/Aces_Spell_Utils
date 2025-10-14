package net.acetheeldritchking.aces_spell_utils.events;

import io.redspace.ironsspellbooks.entity.spells.comet.Comet;
import net.acetheeldritchking.aces_spell_utils.items.example.ExamplePassiveAbilitySpellbook;
import net.acetheeldritchking.aces_spell_utils.registries.ExampleItemRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber
public class SpellbookAbilities {
  
  @SubscribeEvent
    public static void exampleAbility(LivingIncomingDamageEvent event)
    {
        var sheath = ((ExamplePassiveAbilitySpellbook) ExampleItemRegistry.EXAMPLE_PASSIVE_ABILITY_SPELLBOOK.get());
        Entity attacker = event.getSource().getEntity();

        if (attacker instanceof ServerPlayer player)
        {
            if (sheath.isEquippedBy(player))
            {
                if (sheath.tryProcCooldown(player))
                {
                    var victim = event.getEntity();

                    Comet comet = new Comet(player.level(), player);
                    comet.setDamage(5);
                    comet.setPos(victim.getX(), victim.getY() + 7, victim.getZ());
                    var trajectory = new Vec3(0.05F, -0.85F, 0).normalize();
                    comet.shoot(trajectory, 0.045F);
                    comet.setExplosionRadius(4.5F);

                    player.level().addFreshEntity(comet);
                }
            }
        }
    }
}
