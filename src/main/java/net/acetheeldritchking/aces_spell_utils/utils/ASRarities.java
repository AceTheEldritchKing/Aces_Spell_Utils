package net.acetheeldritchking.aces_spell_utils.utils;

import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Rarity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

import java.util.function.UnaryOperator;

public class ASRarities {
    public static final EnumProxy<Rarity> GLACIAL_RARITY_PROXY = new EnumProxy<>(Rarity.class,
            -1,
            "aces_spell_utils:glacial",
            (UnaryOperator<Style>) ((style) -> style.withColor(0x4fc6ff))
    );

    public static final EnumProxy<Rarity> AQUATIC_RARITY_PROXY = new EnumProxy<>(Rarity.class,
            -1,
            "aces_spell_utils:aquatic",
            (UnaryOperator<Style>) ((style) -> style.withColor(0x006aeb))
    );

    public static final EnumProxy<Rarity> VERDANT_RARITY_PROXY = new EnumProxy<>(Rarity.class,
            -1,
            "aces_spell_utils:verdant",
            (UnaryOperator<Style>) ((style) -> style.withColor(0x4de83a))
    );

    public static final EnumProxy<Rarity> COSMIC_RARITY_PROXY = new EnumProxy<>(Rarity.class,
            -1,
            "aces_spell_utils:cosmic",
            (UnaryOperator<Style>) ((style) -> style.withColor(0x9b13e8))
    );

    public static final EnumProxy<Rarity> FORBIDDEN_RARITY_PROXY = new EnumProxy<>(Rarity.class,
            -1,
            "aces_spell_utils:forbidden",
            (UnaryOperator<Style>) ((style) -> style.withColor(0x121f1b))
    );
}
