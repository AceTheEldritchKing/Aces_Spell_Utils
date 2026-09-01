package net.acetheeldritchking.aces_spell_utils.utils;

import net.acetheeldritchking.aces_spell_utils.network.TriggerRibbonPacket;
import net.acetheeldritchking.aces_spell_utils.ribbon.RibbonConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class RibbonHandler {
    // kept server side only so a player who starts tracking the entity later can be told about the ribbon
    private static final Map<UUID, Attached> ATTACHED = new ConcurrentHashMap<>();

    private RibbonHandler() {
    }

    private record Attached(Entity entity, RibbonConfig config) {
    }

    public static void attach(Entity entity, RibbonConfig config) {
        if (entity == null || entity.isRemoved() || entity.level().isClientSide()) {
            return;
        }
        ATTACHED.put(entity.getUUID(), new Attached(entity, config));
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, packet(entity, true, config));
    }

    public static void detach(Entity entity) {
        if (entity == null || entity.level().isClientSide()) {
            return;
        }
        Attached attached = ATTACHED.remove(entity.getUUID());
        if (attached == null) {
            return;
        }
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, packet(entity, false, attached.config()));
    }

    public static boolean isAttached(Entity entity) {
        if (entity == null || entity.level().isClientSide()) {
            return false;
        }
        Attached attached = ATTACHED.get(entity.getUUID());
        return attached != null && attached.entity() == entity;
    }

    @ApiStatus.Internal
    public static void resend(ServerPlayer player, Entity target) {
        Attached attached = ATTACHED.get(target.getUUID());
        if (attached == null || attached.entity() != target) {
            return;
        }
        PacketDistributor.sendToPlayer(player, packet(target, true, attached.config()));
    }

    // a projectile usually expires without anyone calling detach, so dead entries are swept here
    @ApiStatus.Internal
    public static void pruneDead() {
        ATTACHED.values().removeIf(attached -> !attached.entity().isAlive());
    }

    public static void clear() {
        ATTACHED.clear();
    }

    private static TriggerRibbonPacket packet(Entity entity, boolean attach, RibbonConfig config) {
        return new TriggerRibbonPacket(entity.getId(), attach, config);
    }
}
