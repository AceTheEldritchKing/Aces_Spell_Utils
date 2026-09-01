package net.acetheeldritchking.aces_spell_utils.client.dome;

import net.acetheeldritchking.aces_spell_utils.dome.DomeConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@ApiStatus.Internal
public final class DomeManager {
    // a repeating command block would otherwise grow this without bound
    private static final int MAX_ACTIVE = 32;

    private static final List<ActiveDome> ACTIVE = new ArrayList<>();

    private static ClientLevel lastLevel;

    private DomeManager() {
    }

    public static void spawn(Vec3 center, DomeConfig config) {
        if (ACTIVE.size() >= MAX_ACTIVE) {
            ACTIVE.remove(0);
        }
        ACTIVE.add(new ActiveDome(center, config));
    }

    public static void clear() {
        ACTIVE.clear();
    }

    public static List<ActiveDome> active() {
        return ACTIVE;
    }

    // Called once per client tick
    public static void tick() {
        ClientLevel level = Minecraft.getInstance().level;
        // a dimension change swaps the level without disconnecting, and the old centres mean nothing in the new one
        if (level != lastLevel) {
            lastLevel = level;
            ACTIVE.clear();
        }
        if (ACTIVE.isEmpty()) {
            return;
        }
        // a paused game would otherwise age domes out behind the menu, since level ticking stops but this event does not
        if (Minecraft.getInstance().isPaused()) {
            return;
        }
        Iterator<ActiveDome> iterator = ACTIVE.iterator();
        while (iterator.hasNext()) {
            ActiveDome dome = iterator.next();
            dome.tick();
            if (dome.expired()) {
                iterator.remove();
            }
        }
    }
}
