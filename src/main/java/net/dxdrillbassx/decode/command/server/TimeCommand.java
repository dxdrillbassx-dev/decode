package net.dxdrillbassx.decode.command.server;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class TimeCommand {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // Регистрируем команду /time
        dispatcher.register(Commands.literal("time")
                .then(Commands.literal("day")
                        .executes(context -> setTime(context.getSource(), "day"))
                )
                .then(Commands.literal("night")
                        .executes(context -> setTime(context.getSource(), "night"))
                )
                .then(Commands.literal("set")
                        .then(Commands.argument("time", IntegerArgumentType.integer(0))
                                .executes(context -> setTimeToSpecific(context.getSource(), IntegerArgumentType.getInteger(context, "time")))
                        )
                )
        );

        // Альтернативные названия
        dispatcher.register(Commands.literal("etime").redirect(dispatcher.getRoot().getChild("time")));
    }

    private static int setTime(CommandSourceStack source, String timeOfDay) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        Level level = player.getLevel();
        if ("day".equals(timeOfDay)) {
            ((net.minecraft.server.level.ServerLevel) level).setDayTime(1000);
            source.sendSuccess(Component.literal("Время установлено на день!"), true);
        } else if ("night".equals(timeOfDay)) {
            ((net.minecraft.server.level.ServerLevel) level).setDayTime(13000);
            source.sendSuccess(Component.literal("Время установлено на ночь!"), true);
        } else {
            source.sendFailure(Component.literal("Неверный аргумент. Используйте /time day, /time night или /time set <time>."));
            return 0;
        }

        return 1;
    }

    private static int setTimeToSpecific(CommandSourceStack source, int time) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        Level level = player.getLevel();
        ((net.minecraft.server.level.ServerLevel) level).setDayTime(time);
        source.sendSuccess(Component.literal("Время установлено на " + time + "!"), true);

        return 1;
    }
}
