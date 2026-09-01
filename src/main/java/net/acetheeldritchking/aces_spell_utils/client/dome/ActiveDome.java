package net.acetheeldritchking.aces_spell_utils.client.dome;

import net.acetheeldritchking.aces_spell_utils.dome.DomeConfig;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class ActiveDome {
    // the pulse eases in over this fraction rather than snapping on the instant growth ends
    private static final float SETTLE_RAMP = 0.10F;

    private final Vec3 center;
    private final DomeConfig config;
    private int age;

    public ActiveDome(Vec3 center, DomeConfig config) {
        this.center = center;
        this.config = config;
    }

    public Vec3 center() {
        return center;
    }

    public DomeConfig config() {
        return config;
    }

    public void tick() {
        age++;
    }

    public boolean expired() {
        return age >= config.durationTicks();
    }

    // the partial tick is carried through so the shell grows smoothly instead of stepping once per tick
    public float progress(float partialTick) {
        return Mth.clamp(((float) age + partialTick) / (float) config.durationTicks(), 0.0F, 1.0F);
    }

    // the shell reaches full size in the first part of its life and then holds there, rather than creeping outward the whole time
    public float radius(float partialTick) {
        float grow = Mth.clamp(progress(partialTick) / config.settleAt(), 0.0F, 1.0F);
        return config.radius() * config.growth().ease(grow);
    }

    // zero while the shell is still expanding and one once it has settled, so the pulse only runs at full size
    public float settled(float partialTick) {
        return Mth.clamp((progress(partialTick) - config.settleAt()) / SETTLE_RAMP, 0.0F, 1.0F);
    }
}
