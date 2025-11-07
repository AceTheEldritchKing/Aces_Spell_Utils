package net.acetheeldritchking.aces_spell_utils.entity.armor;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.acetheeldritchking.aces_spell_utils.AcesSpellUtils;
import net.acetheeldritchking.aces_spell_utils.items.example.ExampleWarlockArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class ExampleWarlockMaskModel extends DefaultedItemGeoModel<ExampleWarlockArmorItem> {

    public ExampleWarlockMaskModel() {
        super(ResourceLocation.fromNamespaceAndPath(AcesSpellUtils.MOD_ID, ""));
    }

    @Override
    public ResourceLocation getModelResource(ExampleWarlockArmorItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(AcesSpellUtils.MOD_ID, "geo/evil_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ExampleWarlockArmorItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(AcesSpellUtils.MOD_ID, "textures/models/armor/evil_armor.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ExampleWarlockArmorItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(IronsSpellbooks.MODID, "animations/wizard_armor_animation.json");
    }
}
