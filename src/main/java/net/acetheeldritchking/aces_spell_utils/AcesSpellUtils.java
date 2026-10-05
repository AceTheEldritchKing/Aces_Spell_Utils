package net.acetheeldritchking.aces_spell_utils;

import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.render.SpellBookCurioRenderer;
import net.acetheeldritchking.aces_spell_utils.entity.render.items.SheathCurioRenderer;
import net.acetheeldritchking.aces_spell_utils.items.curios.SheathCurioItem;
import net.acetheeldritchking.aces_spell_utils.items.example.items.armor.ExampleArmorMaterialRegistry;
import net.acetheeldritchking.aces_spell_utils.registries.*;
import net.acetheeldritchking.aces_spell_utils.utils.AcesSpellUtilsClientConfig;
import net.acetheeldritchking.aces_spell_utils.utils.AcesSpellUtilsConfig;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.acetheeldritchking.aces_spell_utils.client.particle.TrailParticle;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

import static net.acetheeldritchking.aces_spell_utils.utils.AcesSpellUtilsConfig.*;
import static net.acetheeldritchking.aces_spell_utils.utils.AcesSpellUtilsConfig.DEV_MODE;
import static net.acetheeldritchking.aces_spell_utils.utils.AcesSpellUtilsConfig.MANA_REND_WHITELIST;
import static net.acetheeldritchking.aces_spell_utils.utils.AcesSpellUtilsConfig.MANA_STEAL_WHITELIST;
import static net.acetheeldritchking.aces_spell_utils.utils.AcesSpellUtilsConfig.REFINEMENT_DIFFERENCE;
import static net.acetheeldritchking.aces_spell_utils.utils.AcesSpellUtilsConfig.devMode;
import static net.acetheeldritchking.aces_spell_utils.utils.AcesSpellUtilsConfig.manaRendWhitelist;
import static net.acetheeldritchking.aces_spell_utils.utils.AcesSpellUtilsConfig.manaStealWhitelist;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(AcesSpellUtils.MOD_ID)
public class AcesSpellUtils {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "aces_spell_utils";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public AcesSpellUtils(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        ASAttributeRegistry.register(modEventBus);
        ASSchoolRegistry.register(modEventBus);
        ASParticleRegistry.register(modEventBus);
        ASAttachmentRegistry.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);

        // We're only gonna register the creative menu if you're in a dev environment
        if (registerExamplesInDev())
        {
            // Creative Tab
            ASCreativeModeTabs.register(modEventBus);
        }

        // Keeping the items out, smth smth unbound value accessed
        ExampleItemRegistry.register(modEventBus);
        ExampleArmorMaterialRegistry.register(modEventBus);
        ExampleSpellRegistry.register(modEventBus);

        // Configs
        modContainer.registerConfig(ModConfig.Type.CLIENT, AcesSpellUtilsClientConfig.SPEC, String.format("%s-client.toml", AcesSpellUtils.MOD_ID));
        modContainer.registerConfig(ModConfig.Type.SERVER, AcesSpellUtilsConfig.SPEC, String.format("%s-common.toml", AcesSpellUtils.MOD_ID));
    }

    static boolean registerExamplesInDev()
    {
        return !FMLEnvironment.production;
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
    }

    @EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // curios
            event.enqueueWork(() -> {
                // Spellbook on player
                ExampleItemRegistry.getASUItems().stream().filter(item -> item.get() instanceof SpellBook).forEach((item) -> CuriosRendererRegistry.register(item.get(), SpellBookCurioRenderer::new));
                // Rendering sheath on the player
                ExampleItemRegistry.getASUItems().stream().filter(item -> item.get() instanceof SheathCurioItem).forEach((item) -> CuriosRendererRegistry.register(item.get(), SheathCurioRenderer::new));
            });
        }

        @SubscribeEvent
        public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event)
        {
            event.registerSpriteSet(ASParticleRegistry.TRAIL.get(), TrailParticle.Provider::new);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // things in here
    }

    public static ResourceLocation id(@NotNull String path)
    {
        return ResourceLocation.fromNamespaceAndPath(AcesSpellUtils.MOD_ID, path);
    }
}
