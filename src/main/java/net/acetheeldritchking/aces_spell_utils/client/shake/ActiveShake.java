package net.acetheeldritchking.aces_spell_utils.client.shake;

import net.acetheeldritchking.aces_spell_utils.shake.ShakeConfig;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class ActiveShake {
    private final Vec3 center;
    private final ShakeConfig config;
    private int age;

    public ActiveShake(Vec3 center, ShakeConfig config) {
        this.center = center;
        this.config = config;
    }

    public void tick() {
        age++;
    }

    public boolean expired() {
        return age > config.durationTicks() + config.fadeTicks();
    }

    // the viewer's own distance decides how hard this one hits them, so amplitude is asked for per frame rather than stored
    public float amplitudeAt(Vec3 viewer, float partialTick) {
        float elapsed = (float) age + partialTick;
        float held = config.magnitude();
        if (elapsed > config.durationTicks()) {
            // a squared tail decays fast at first and settles gently, which reads as the ground going quiet
            float tail = 1.0F - (elapsed - config.durationTicks()) / (float) config.fadeTicks();
            if (tail <= 0.0F) {
                return 0.0F;
            }
            held *= tail * tail;
        }
        float near = 1.0F - Mth.clamp((float) (center.distanceTo(viewer) / config.radius()), 0.0F, 1.0F);
        // squared so the shake stays local instead of rattling the whole render distance evenly
        return held * near * near;
    }
}
