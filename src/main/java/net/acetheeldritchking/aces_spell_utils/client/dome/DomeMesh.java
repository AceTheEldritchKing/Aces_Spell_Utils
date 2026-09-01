package net.acetheeldritchking.aces_spell_utils.client.dome;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;

@ApiStatus.Internal
public final class DomeMesh {
    private static final int SEGMENTS = 64;
    private static final int RINGS = 16;
    private static final int VERTEX_COUNT = SEGMENTS * RINGS * 4;

    private static final float ENGULF_WASH = 0.25F;

    // a unit sphere doubles as its own normals, so one array serves both
    private static final float[] POSITIONS = buildPositions();

    private DomeMesh() {
    }

    private static float[] buildPositions() {
        float[] out = new float[VERTEX_COUNT * 3];
        int i = 0;
        for (int ring = 0; ring < RINGS; ring++) {
            double phi0 = Math.PI * ring / RINGS;
            double phi1 = Math.PI * (ring + 1) / RINGS;
            for (int seg = 0; seg < SEGMENTS; seg++) {
                double theta0 = 2.0 * Math.PI * seg / SEGMENTS;
                double theta1 = 2.0 * Math.PI * (seg + 1) / SEGMENTS;
                i = put(out, i, phi0, theta0);
                i = put(out, i, phi1, theta0);
                i = put(out, i, phi1, theta1);
                i = put(out, i, phi0, theta1);
            }
        }
        return out;
    }

    // built with double trig rather than Mth, since this runs once and the table lookups would show as facets
    private static int put(float[] out, int i, double phi, double theta) {
        double sinPhi = Math.sin(phi);
        out[i++] = (float) (sinPhi * Math.cos(theta));
        out[i++] = (float) Math.cos(phi);
        out[i++] = (float) (sinPhi * Math.sin(theta));
        return i;
    }

    public static void emit(Matrix4f matrix, VertexConsumer consumer, Vec3 relativeCenter, float radius,
                            int rgb, float alphaScale, float rimPower, float crownFade) {
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        float cx = (float) relativeCenter.x;
        float cy = (float) relativeCenter.y;
        float cz = (float) relativeCenter.z;
        // a shell that has swallowed the camera has no silhouette left, so it washes out instead of winking out
        float centerDist = (float) relativeCenter.length();
        float engulf = radius > 1.0E-4F ? Mth.clamp(1.0F - centerDist / radius, 0.0F, 1.0F) * ENGULF_WASH : 0.0F;
        for (int v = 0; v < VERTEX_COUNT; v++) {
            int p = v * 3;
            float nx = POSITIONS[p];
            float ny = POSITIONS[p + 1];
            float nz = POSITIONS[p + 2];
            float x = cx + nx * radius;
            float y = cy + ny * radius;
            float z = cz + nz * radius;
            // positions are already camera relative, so the camera sits at the origin and the view vector is just -vertex
            float len = Mth.sqrt(x * x + y * y + z * z);
            // a vertex sitting exactly on the camera has no view direction, so treat it as fully edge on
            float facing = len < 1.0E-4F ? 0.0F : Math.abs((nx * x + ny * y + nz * z) / len);
            float rim = Math.max((float) Math.pow(1.0F - facing, rimPower), engulf);
            // the crown runs faintest and the base brightest, which is the gradient the reference shows up the arch
            float crown = 1.0F - crownFade * (0.5F + 0.5F * ny);
            int alpha = Mth.clamp(Math.round(rim * crown * alphaScale * 255.0F), 0, 255);
            consumer.addVertex(matrix, x, y, z).setColor(red, green, blue, alpha);
        }
    }

}
