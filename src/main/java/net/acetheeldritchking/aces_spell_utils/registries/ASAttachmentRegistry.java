package net.acetheeldritchking.aces_spell_utils.registries;

import com.mojang.serialization.Codec;
import net.acetheeldritchking.aces_spell_utils.AcesSpellUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ASAttachmentRegistry {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, AcesSpellUtils.MOD_ID);

    public static final Supplier<AttachmentType<Boolean>> KEEP_INV_ON_DEATH = ATTACHMENT_TYPES.register(
            "keep_inv_on_death", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).copyOnDeath().build());

    public static void register(IEventBus eventBus)
    {
        ATTACHMENT_TYPES.register(eventBus);
    }
}
