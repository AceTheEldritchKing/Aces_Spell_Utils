package net.acetheeldritchking.aces_spell_utils.client.shake;

import net.acetheeldritchking.aces_spell_utils.shake.ShakeConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@ApiStatus.Internal
public final class ShakeManager {
    // a repeating command block would otherwise grow this without bound
    private static final int MAX_ACTIVE = 32;
    // past this the summed swing stops reading as impact and starts making the game unplayable
    private static final float MAX_AMPLITUDE = 12.0F;

    private static final List<ActiveShake> ACTIVE = new ArrayList<>();
    private static ClientLevel lastLevel;

    private ShakeManager() {
    }

    public static void spawn(Vec3 center, ShakeConfig config) {
        if (ACTIVE.size() >= MAX_ACTIVE) {
            ACTIVE.remove(0);
        }
        ACTIVE.add(new ActiveShake(center, config));
    }

    public static void clear() {
        ACTIVE.clear();
    }

    // Called once per client tick
    public static void tick() {
        ClientLevel level = Minecraft.getInstance().level;
        // a dimension change swaps the level without disconnecting, and an old epicentre means nothing in the new one
        if (level != lastLevel) {
            lastLevel = level;
            ACTIVE.clear();
        }
        if (ACTIVE.isEmpty()) {
            return;
        }
        // a paused game would otherwise age shakes out behind the menu
        if (Minecraft.getInstance().isPaused()) {
            return;
        }
        Iterator<ActiveShake> iterator = ACTIVE.iterator();
        while (iterator.hasNext()) {
            ActiveShake shake = iterator.next();
            shake.tick();
            if (shake.expired()) {
                iterator.remove();
            }
        }
    }

    public static float amplitude(Vec3 viewer, float partialTick) {
        if (ACTIVE.isEmpty()) {
            return 0.0F;
        }
        float total = 0.0F;
        for (ActiveShake shake : ACTIVE) {
            total += shake.amplitudeAt(viewer, partialTick);
        }
        return Math.min(total, MAX_AMPLITUDE);
    }
}
