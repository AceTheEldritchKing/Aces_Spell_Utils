package net.acetheeldritchking.aces_spell_utils.items.example;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.acetheeldritchking.aces_spell_utils.AcesSpellUtils;
import net.acetheeldritchking.aces_spell_utils.entity.armor.ExampleWarlockMaskModel;
import net.acetheeldritchking.aces_spell_utils.entity.render.armor.EmissiveGenericCustomArmorRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class ExampleWarlockArmorItem extends ImbuableExtendedGeoArmorItem {
    public ExampleWarlockArmorItem(ArmorItem.Type slot, Item.Properties settings) {
        super(ExampleArmorMaterialRegistry.EXAMPLE_ARMOR, slot, settings, schoolAttributesWithResistance(AttributeRegistry.ELDRITCH_SPELL_POWER, AttributeRegistry.MANA_REGEN, 150, 0.10F, 0.05F, 0.05F));
    }

    private static final ResourceLocation LAYER = ResourceLocation.fromNamespaceAndPath(
            AcesSpellUtils.MOD_ID,
            "textures/models/armor/evil_armor_tips.png");

    @Override
    @OnlyIn(Dist.CLIENT)
    public GeoArmorRenderer<?> supplyRenderer() {
        RenderType GLOW_RENDER_TYPE = RenderType.breezeEyes(LAYER);

        return new EmissiveGenericCustomArmorRenderer<>(new ExampleWarlockMaskModel(), LAYER, GLOW_RENDER_TYPE);
    }
}
