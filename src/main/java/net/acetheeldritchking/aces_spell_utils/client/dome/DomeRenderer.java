package net.acetheeldritchking.aces_spell_utils.client.dome;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.acetheeldritchking.aces_spell_utils.dome.DomeConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;

@ApiStatus.Internal
public final class DomeRenderer {
    private DomeRenderer() {
    }

    public static void render(PoseStack poseStack, Vec3 cam, float partialTick) {
        if (DomeManager.active().isEmpty()) {
            return;
        }
        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer consumer = buffers.getBuffer(DomeRenderTypes.DOME);
        Matrix4f matrix = poseStack.last().pose();
        double cutoff = Minecraft.getInstance().options.getEffectiveRenderDistance() * 16.0;
        for (ActiveDome dome : DomeManager.active()) {
            float progress = dome.progress(partialTick);
            float radius = dome.radius(partialTick);
            DomeConfig config = dome.config();
            float alpha = config.shell().alpha().at(progress);
            if (alpha <= 0.0F || radius <= 0.0F) {
                continue;
            }
            // a distant dome is skipped by its nearest point rather than its centre, since a wide shell can reach the player from far off
            double reach = cutoff + radius;
            if (dome.center().distanceToSqr(cam) > reach * reach) {
                continue;
            }
            // the pulse runs on elapsed ticks, so it breathes at a fixed rate rather than with the dome's own duration
            float elapsed = progress * config.durationTicks();
            // the silhouette holds still and only the falloff sharpens, so the band thins and thickens in place once settled
            float pulse = config.pulse().amount() * dome.settled(partialTick) * Mth.sin(elapsed * config.pulse().speed());
            float rimPower = Math.max(0.1F, config.shell().rimPower() + pulse);
            DomeMesh.emit(matrix, consumer, dome.center().subtract(cam), radius,
                    config.shell().color().at(progress), alpha, rimPower, config.shell().crownFade());
        }
        buffers.endBatch(DomeRenderTypes.DOME);
    }
}
