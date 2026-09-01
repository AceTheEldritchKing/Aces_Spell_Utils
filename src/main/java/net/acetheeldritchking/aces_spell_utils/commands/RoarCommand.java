package net.acetheeldritchking.aces_spell_utils.commands;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.acetheeldritchking.aces_spell_utils.AcesSpellUtils;
import net.acetheeldritchking.aces_spell_utils.dome.DomeConfig;
import net.acetheeldritchking.aces_spell_utils.ribbon.Easing;
import net.acetheeldritchking.aces_spell_utils.roar.RoarConfig;
import net.acetheeldritchking.aces_spell_utils.shake.ShakeConfig;
import net.acetheeldritchking.aces_spell_utils.utils.DomeHandler;
import net.acetheeldritchking.aces_spell_utils.utils.ShakeHandler;
import net.acetheeldritchking.aces_spell_utils.utils.RoarHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.Collection;

@EventBusSubscriber(modid = AcesSpellUtils.MOD_ID)
public final class RoarCommand {
    private RoarCommand() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(build());
    }

    private static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("roar")
                // level 2 is what a command block runs at, so this stays usable from one without opening it to everyone
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("zoom")
                        .then(zoomArgs(target(), RoarCommand::dispatchEntity))
                        // "at" pins the roar to a position, so a command block roars from itself and execute at retargets it
                        .then(Commands.literal("at").then(zoomArgs(pos(), RoarCommand::dispatchPos))))
                .then(Commands.literal("ring")
                        .then(ringArgs(target(), RoarCommand::dispatchEntity))
                        .then(Commands.literal("at").then(ringArgs(pos(), RoarCommand::dispatchPos))))
                .then(Commands.literal("shake")
                        .then(Commands.literal("at")
                                .then(pos()
                                        .executes(ctx -> shake(ctx, 24.0F, 1.6F, 10))
                                        .then(Commands.argument("radius", FloatArgumentType.floatArg(1.0F, 256.0F))
                                                .then(Commands.argument("magnitude", FloatArgumentType.floatArg(0.0F, 20.0F))
                                                        .then(Commands.argument("duration", IntegerArgumentType.integer(0, 20 * 60))
                                                                .executes(ctx -> shake(ctx,
                                                                        FloatArgumentType.getFloat(ctx, "radius"),
                                                                        FloatArgumentType.getFloat(ctx, "magnitude"),
                                                                        IntegerArgumentType.getInteger(ctx, "duration")))))))))
                .then(Commands.literal("dome")
                        .then(Commands.literal("at")
                                .then(pos()
                                        .executes(ctx -> dome(ctx, 8.0F, 200))
                                        .then(Commands.argument("radius", FloatArgumentType.floatArg(0.5F, 128.0F))
                                                .then(Commands.argument("duration", IntegerArgumentType.integer(1, 20 * 60))
                                                        .executes(ctx -> dome(ctx,
                                                                FloatArgumentType.getFloat(ctx, "radius"),
                                                                IntegerArgumentType.getInteger(ctx, "duration"))))))));
    }

    private static RequiredArgumentBuilder<CommandSourceStack, ?> target() {
        return Commands.argument("source", EntityArgument.entities());
    }

    private static RequiredArgumentBuilder<CommandSourceStack, ?> pos() {
        return Commands.argument("pos", Vec3Argument.vec3());
    }

    private static <T extends ArgumentBuilder<CommandSourceStack, T>> T zoomArgs(T root, Dispatcher dispatcher) {
        return root
                .executes(ctx -> dispatcher.run(ctx, RoarConfig.zoom(0.9F, 0.6F, 30, Easing.EASE_OUT_QUAD)))
                .then(Commands.argument("strength", FloatArgumentType.floatArg(0.0F, 1.0F))
                        .then(Commands.argument("sharpness", FloatArgumentType.floatArg(0.0F, 8.0F))
                                .then(Commands.argument("duration", IntegerArgumentType.integer(1, 20 * 60))
                                        .executes(ctx -> dispatcher.run(ctx, RoarConfig.zoom(
                                                FloatArgumentType.getFloat(ctx, "strength"),
                                                FloatArgumentType.getFloat(ctx, "sharpness"),
                                                IntegerArgumentType.getInteger(ctx, "duration"),
                                                Easing.EASE_OUT_QUAD))))));
    }

    private static <T extends ArgumentBuilder<CommandSourceStack, T>> T ringArgs(T root, Dispatcher dispatcher) {
        return root
                .executes(ctx -> dispatcher.run(ctx, RoarConfig.ring(1.2F, 0.18F, 0.3F, 0.12F, 30, Easing.EASE_OUT_QUAD)))
                .then(Commands.argument("radius", FloatArgumentType.floatArg(0.0F, 2.0F))
                        .then(Commands.argument("thickness", FloatArgumentType.floatArg(0.01F, 1.0F))
                                .then(Commands.argument("blur", FloatArgumentType.floatArg(0.0F, 0.5F))
                                        .then(Commands.argument("refraction", FloatArgumentType.floatArg(0.0F, 0.5F))
                                                .then(Commands.argument("duration", IntegerArgumentType.integer(1, 20 * 60))
                                                        .executes(ctx -> dispatcher.run(ctx, RoarConfig.ring(
                                                                FloatArgumentType.getFloat(ctx, "radius"),
                                                                FloatArgumentType.getFloat(ctx, "thickness"),
                                                                FloatArgumentType.getFloat(ctx, "blur"),
                                                                FloatArgumentType.getFloat(ctx, "refraction"),
                                                                IntegerArgumentType.getInteger(ctx, "duration"),
                                                                Easing.EASE_OUT_QUAD))))))));
    }

    private static int dome(CommandContext<CommandSourceStack> ctx, float radius, int duration) throws CommandSyntaxException {
        Vec3 pos = Vec3Argument.getVec3(ctx, "pos");
        DomeHandler.trigger(ctx.getSource().getLevel(), pos, DomeConfig.of(0xFF2020, radius, duration));
        ctx.getSource().sendSuccess(() -> Component.literal("Triggered dome at " + pos), true);
        return 1;
    }

    private static int shake(CommandContext<CommandSourceStack> ctx, float radius, float magnitude, int duration) throws CommandSyntaxException {
        Vec3 pos = Vec3Argument.getVec3(ctx, "pos");
        ShakeHandler.trigger(ctx.getSource().getLevel(), pos, new ShakeConfig(radius, magnitude, duration, 10));
        ctx.getSource().sendSuccess(() -> Component.literal("Triggered shake at " + pos), true);
        return 1;
    }


    private static int dispatchPos(CommandContext<CommandSourceStack> ctx, RoarConfig config) throws CommandSyntaxException {
        Vec3 pos = Vec3Argument.getVec3(ctx, "pos");
        RoarHandler.trigger(ctx.getSource().getLevel(), pos, config);
        ctx.getSource().sendSuccess(() -> Component.literal("Triggered roar at " + pos), true);
        return 1;
    }

    private static int dispatchEntity(CommandContext<CommandSourceStack> ctx, RoarConfig config) throws CommandSyntaxException {
        Collection<? extends Entity> sources = EntityArgument.getEntities(ctx, "source");
        for (Entity source : sources) {
            RoarHandler.trigger(source, config);
        }
        int count = sources.size();
        ctx.getSource().sendSuccess(() -> Component.literal("Triggered roar on " + count + " entity(s)"), true);
        return count;
    }

    @FunctionalInterface
    private interface Dispatcher {
        int run(CommandContext<CommandSourceStack> ctx, RoarConfig config) throws CommandSyntaxException;
    }
}
