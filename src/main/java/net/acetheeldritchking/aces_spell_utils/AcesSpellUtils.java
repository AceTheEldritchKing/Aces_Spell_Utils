package net.acetheeldritchking.aces_spell_utils;

import net.acetheeldritchking.aces_spell_utils.registries.ASAttributeRegistry;
import net.acetheeldritchking.aces_spell_utils.utils.AcesSpellUtilsConfig;
import net.minecraft.resources.ResourceLocation;
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

        NeoForge.EVENT_BUS.register(this);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        //modContainer.registerConfig(ModConfig.Type.COMMON, AcesSpellUtilsConfig.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
    }


    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
    }

    public static ResourceLocation id(@NotNull String path)
    {
        return ResourceLocation.fromNamespaceAndPath(AcesSpellUtils.MOD_ID, path);
    }
}
