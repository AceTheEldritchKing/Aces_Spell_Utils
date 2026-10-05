package net.acetheeldritchking.aces_spell_utils.entity.render.armor;

import io.redspace.ironslib.transmog.client.TransmogArmorRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.model.GeoModel;

public class EmissiveTransmogArmorRenderer<T extends Item & GeoItem> extends TransmogArmorRenderer<T> {
    public EmissiveTransmogArmorRenderer(GeoModel<T> model, ResourceLocation glowLayer, RenderType renderType) {
        super(model);
        this.addRenderLayer(new GeoArmorGlowmaskLayer<>(this, glowLayer, renderType));
    }

    @Override
    public RenderType getRenderType(T animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(this.getTextureLocation(animatable));
    }
}
