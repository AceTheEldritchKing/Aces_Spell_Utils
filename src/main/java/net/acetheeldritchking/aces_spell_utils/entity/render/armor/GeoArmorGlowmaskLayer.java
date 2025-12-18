package net.acetheeldritchking.aces_spell_utils.entity.render.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class GeoArmorGlowmaskLayer<T extends GeoAnimatable> extends GeoRenderLayer<T> {
    public final ResourceLocation GLOW_LAYER;
    public final RenderType RENDER_TYPE;

    public GeoArmorGlowmaskLayer(GeoRenderer<T> entityRendererIn, ResourceLocation glowMask, RenderType renderType) {
        super(entityRendererIn);
        this.GLOW_LAYER = glowMask;
        this.RENDER_TYPE = renderType;
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        //RenderType glowRenderType = RenderType.eyes(GLOW_LAYER);

        this.getRenderer()
                .reRender(this.getDefaultBakedModel(animatable),
                        poseStack,
                        bufferSource,
                        animatable,
                        RENDER_TYPE,
                        bufferSource.getBuffer(RENDER_TYPE),
                        partialTick,
                        packedLight,
                        OverlayTexture.NO_OVERLAY,
                        0xFFFFFFFF
                );
    }
}
