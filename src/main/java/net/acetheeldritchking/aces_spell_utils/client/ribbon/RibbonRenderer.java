package net.acetheeldritchking.aces_spell_utils.client.ribbon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.acetheeldritchking.aces_spell_utils.ribbon.RibbonConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;

import java.util.List;
import java.util.Map;

@ApiStatus.Internal
public final class RibbonRenderer {
    private RibbonRenderer() {
    }

    public static void render(PoseStack poseStack, Vec3 cam, float partialTick) {
        if (RibbonManager.active().isEmpty()) {
            return;
        }
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        Matrix4f matrix = poseStack.last().pose();
        // each pass grabs its own consumer and finishes with it before the next pass starts, since both
        // ribbon types share one buffer and requesting a second consumer flushes the first mid-batch
        renderPass(matrix, buffers, cam, partialTick, level, false);
        renderPass(matrix, buffers, cam, partialTick, level, true);
        // ending a type that received no geometry is a no-op, so both are flushed rather than tracked
        buffers.endBatch(RibbonRenderTypes.RIBBON);
        buffers.endBatch(RibbonRenderTypes.RIBBON_ADDITIVE);
    }

    private static void renderPass(Matrix4f matrix, MultiBufferSource.BufferSource buffers, Vec3 cam, float partialTick, ClientLevel level, boolean additive) {
        RenderType type = additive ? RibbonRenderTypes.RIBBON_ADDITIVE : RibbonRenderTypes.RIBBON;
        VertexConsumer consumer = buffers.getBuffer(type);
        for (Map.Entry<Integer, ActiveRibbon> entry : RibbonManager.active().entrySet()) {
            ActiveRibbon ribbon = entry.getValue();
            if (ribbon.config().additive() != additive) {
                continue;
            }
            Entity entity = level.getEntity(entry.getKey());
            Vec3 head = entity == null ? null : entity.getPosition(partialTick);
            emit(matrix, consumer, ribbon, cam, head);
        }
    }

    private static Vec3 pointAt(List<Vec3> points, int i, int last, Vec3 head) {
        return i == last && head != null ? head : points.get(i);
    }

    private static Vec3 direction(Vec3 from, Vec3 to) {
        Vec3 seg = to.subtract(from);
        double len = seg.length();
        return len < 1.0E-6 ? null : seg.scale(1.0 / len);
    }

    private static Vec3 normalized(Vec3 vec) {
        double len = vec.length();
        return len < 1.0E-6 ? null : vec.scale(1.0 / len);
    }

    private static void emit(Matrix4f matrix, VertexConsumer consumer, ActiveRibbon ribbon, Vec3 cam, Vec3 head) {
        List<Vec3> points = ribbon.points();
        int last = points.size() - 1;
        if (last < 1) {
            return;
        }
        RibbonConfig config = ribbon.config();

        Vec3[] at = new Vec3[last + 1];
        for (int i = 0; i <= last; i++) {
            // the head point is interpolated with the partial tick so the strip stays attached to the entity between ticks
            at[i] = pointAt(points, i, last, head).subtract(cam);
        }

        // one side vector per point, shared by both quads meeting there, so a bend cannot open a seam
        Vec3[] sides = new Vec3[last + 1];
        double[] miters = new double[last + 1];
        for (int i = 0; i <= last; i++) {
            Vec3 in = i > 0 ? direction(at[i - 1], at[i]) : null;
            Vec3 out = i < last ? direction(at[i], at[i + 1]) : null;
            Vec3 dir = in == null ? out : (out == null ? in : normalized(in.add(out)));
            // a point sitting on the camera has no view vector to build a perpendicular from
            if (dir == null || at[i].lengthSqr() < 1.0E-12) {
                continue;
            }
            sides[i] = normalized(dir.cross(at[i].scale(-1.0).normalize()));
            double half = in == null || out == null ? 1.0 : dir.dot(out);
            // a bend thins the strip unless the joint widens to meet it, capped so a hairpin cannot spike
            miters[i] = half < 0.5 ? 2.0 : 1.0 / half;
        }

        for (int i = 0; i < last; i++) {
            if (sides[i] == null || sides[i + 1] == null) {
                continue;
            }
            Vec3 a = at[i];
            Vec3 c = at[i + 1];
            float t0 = (float) i / last;
            float t1 = (float) (i + 1) / last;
            Vec3 s0 = sides[i].scale(config.width().at(t0) * miters[i]);
            Vec3 s1 = sides[i + 1].scale(config.width().at(t1) * miters[i + 1]);
            int c0 = config.color().at(t0);
            int c1 = config.color().at(t1);
            int r0 = (c0 >> 16) & 0xFF;
            int g0 = (c0 >> 8) & 0xFF;
            int b0 = c0 & 0xFF;
            int r1 = (c1 >> 16) & 0xFF;
            int g1 = (c1 >> 8) & 0xFF;
            int b1 = c1 & 0xFF;
            int a0 = Mth.clamp(Math.round(config.alpha().at(t0) * 255.0F), 0, 255);
            int a1 = Mth.clamp(Math.round(config.alpha().at(t1) * 255.0F), 0, 255);
            consumer.addVertex(matrix, (float) (a.x - s0.x), (float) (a.y - s0.y), (float) (a.z - s0.z)).setColor(r0, g0, b0, a0);
            consumer.addVertex(matrix, (float) (a.x + s0.x), (float) (a.y + s0.y), (float) (a.z + s0.z)).setColor(r0, g0, b0, a0);
            consumer.addVertex(matrix, (float) (c.x + s1.x), (float) (c.y + s1.y), (float) (c.z + s1.z)).setColor(r1, g1, b1, a1);
            consumer.addVertex(matrix, (float) (c.x - s1.x), (float) (c.y - s1.y), (float) (c.z - s1.z)).setColor(r1, g1, b1, a1);
        }
    }
}
