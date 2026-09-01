package net.acetheeldritchking.aces_spell_utils.client.ribbon;

import net.acetheeldritchking.aces_spell_utils.ribbon.RibbonConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@ApiStatus.Internal
public final class RibbonManager {
    private static final Map<Integer, ActiveRibbon> ACTIVE = new HashMap<>();

    private RibbonManager() {
    }

    public static void attach(int entityId, RibbonConfig config) {
        ACTIVE.put(entityId, new ActiveRibbon(config));
    }

    public static void detach(int entityId) {
        ACTIVE.remove(entityId);
    }

    public static void clear() {
        ACTIVE.clear();
    }

    public static Map<Integer, ActiveRibbon> active() {
        return ACTIVE;
    }

    // Called once per client tick
    public static void tick() {
        if (ACTIVE.isEmpty()) {
            return;
        }
        // a paused game would otherwise flood the history with duplicate points since level ticking stops but this event doesn't
        if (Minecraft.getInstance().isPaused()) {
            return;
        }
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            ACTIVE.clear();
            return;
        }
        Iterator<Map.Entry<Integer, ActiveRibbon>> iterator = ACTIVE.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, ActiveRibbon> entry = iterator.next();
            Entity entity = level.getEntity(entry.getKey());
            if (entity == null || !entity.isAlive()) {
                iterator.remove();
                continue;
            }
            entry.getValue().sample(entity.position());
        }
    }
}
