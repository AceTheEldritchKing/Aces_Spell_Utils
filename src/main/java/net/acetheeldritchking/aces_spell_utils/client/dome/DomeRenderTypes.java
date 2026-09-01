package net.acetheeldritchking.aces_spell_utils.client.dome;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

public final class DomeRenderTypes {
    public static final RenderType DOME = RenderType.create(
            "aces_spell_utils_dome",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS,
            32768,
            false,
            // the blend is additive in the destination, so the result does not depend on draw order
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
                    // lightning (not additive) blend glows while still respecting vertex alpha, which is the whole fresnel term
                    .setTransparencyState(RenderStateShard.LIGHTNING_TRANSPARENCY)
                    // the far hemisphere has to draw, since its rim is half of what makes the shell read as a ring
                    .setCullState(RenderStateShard.NO_CULL)
                    .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                    // colour only, so overlapping shell faces blend instead of z fighting
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .createCompositeState(false));

    private DomeRenderTypes() {
    }
}
