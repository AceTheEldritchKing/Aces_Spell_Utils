package net.acetheeldritchking.aces_spell_utils.entity.render.armor;

import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.model.GeoModel;

public class EmissiveGenericCustomArmorRenderer<T extends Item & GeoItem> extends GenericCustomArmorRenderer<T> {
    public EmissiveGenericCustomArmorRenderer(GeoModel<T> model, ResourceLocation glowLayer) {
        super(model);
        this.addRenderLayer(new GeoArmorGlowmaskLayer<>(this, glowLayer));
    }

    @Override
    public RenderType getRenderType(T animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(this.getTextureLocation(animatable));
    }
}
