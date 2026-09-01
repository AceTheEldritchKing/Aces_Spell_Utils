package net.acetheeldritchking.aces_spell_utils.client.ribbon;

import net.acetheeldritchking.aces_spell_utils.ribbon.RibbonConfig;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class ActiveRibbon {
    private static final double TELEPORT_DISTANCE_SQR = 1024.0;

    private final RibbonConfig config;
    private final List<Vec3> points = new ArrayList<>();

    public ActiveRibbon(RibbonConfig config) {
        this.config = config;
    }

    public RibbonConfig config() {
        return config;
    }

    public List<Vec3> points() {
        return points;
    }

    public void sample(Vec3 pos) {
        // a teleport is not movement, so restart rather than stretching the ribbon across the world
        if (!points.isEmpty() && points.get(points.size() - 1).distanceToSqr(pos) > TELEPORT_DISTANCE_SQR) {
            points.clear();
        }
        points.add(pos);
        while (points.size() > config.length()) {
            points.remove(0);
        }
    }
}
