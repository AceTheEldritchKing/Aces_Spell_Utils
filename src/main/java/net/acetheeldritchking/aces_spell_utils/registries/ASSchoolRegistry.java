package net.acetheeldritchking.aces_spell_utils.registries;

import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.acetheeldritchking.aces_spell_utils.AcesSpellUtils;
import net.acetheeldritchking.aces_spell_utils.utils.ASTags;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static io.redspace.ironsspellbooks.api.registry.SchoolRegistry.SCHOOL_REGISTRY_KEY;

public class ASSchoolRegistry {
    private static final DeferredRegister<SchoolType> ASU_SCHOOLS = DeferredRegister.create(SCHOOL_REGISTRY_KEY, AcesSpellUtils.MOD_ID);

    public static void register(IEventBus eventBus)
    {
        ASU_SCHOOLS.register(eventBus);
    }

    private static Supplier<SchoolType> registerSchool(SchoolType type)
    {
        return ASU_SCHOOLS.register(type.getId().getPath(), () -> type);
    }

    // Ritual
    public static final ResourceLocation RITUAL_RESOURCE = AcesSpellUtils.id("ritual");

    public static final Supplier<SchoolType> RITUAL = registerSchool(new SchoolType
            (
                    RITUAL_RESOURCE,
                    ASTags.RITUAL_FOCUS,
                    Component.translatable("school.aces_spell_utils.ritual").withStyle(Style.EMPTY.withColor(0x870b32)),
                    ASAttributeRegistry.RITUAL_MAGIC_POWER,
                    ASAttributeRegistry.RITUAL_MAGIC_RESIST,
                    SoundRegistry.EVOCATION_CAST,
                    ASDamageTypes.RITUAL_MAGIC
            ));

    // Abyssal
    public static final ResourceLocation HYDRO_RESOURCE = AcesSpellUtils.id("hydro");

    public static final Supplier<SchoolType> ABYSSAL = registerSchool(new SchoolType
            (
                    HYDRO_RESOURCE,
                    ASTags.HYDRO_FOCUS,
                    Component.translatable("school.aces_spell_utils.hydro").withStyle(Style.EMPTY.withColor(0x36156c)),
                    ASAttributeRegistry.HYDRO_MAGIC_POWER,
                    ASAttributeRegistry.HYDRO_MAGIC_RESIST,
                    SoundRegistry.EVOCATION_CAST,
                    ASDamageTypes.HYDRO_MAGIC
            ));

    // Technomancy
    public static final ResourceLocation TECHNOMANCY_RESOURCE = AcesSpellUtils.id("technomancy");

    public static final Supplier<SchoolType> TECHNOMANCY = registerSchool(new SchoolType
            (
                    TECHNOMANCY_RESOURCE,
                    ASTags.TECHNOMANCY_FOCUS,
                    Component.translatable("school.aces_spell_utils.technomancy").withStyle(Style.EMPTY.withColor(0xb3bec5)),
                    ASAttributeRegistry.TECHNOMANCY_MAGIC_POWER,
                    ASAttributeRegistry.TECHNOMANCY_MAGIC_RESIST,
                    SoundRegistry.EVOCATION_CAST,
                    ASDamageTypes.TECHNOMANCY_MAGIC
            ));
}
