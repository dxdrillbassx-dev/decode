package net.dxdrillbassx.decode.command.server;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class WeatherCommand {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // Регистрируем команду /weather
        dispatcher.register(Commands.literal("weather")
                .then(Commands.literal("clear")
                        .executes(context -> setWeather(context.getSource(), "clear"))
                )
                .then(Commands.literal("rain")
                        .executes(context -> setWeather(context.getSource(), "rain"))
                )
                .then(Commands.literal("thunder")
                        .executes(context -> setWeather(context.getSource(), "thunder"))
                )
                .then(Commands.argument("type", StringArgumentType.word())
                        .executes(context -> setWeather(context.getSource(), StringArgumentType.getString(context, "type")))
                )
        );
    }

    private static int setWeather(CommandSourceStack source, String weatherType) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        ServerLevel level = player.getLevel();  // Убедитесь, что получаете серверный уровень

        switch (weatherType.toLowerCase()) {
            case "clear":
                setClearWeather(level);
                break;
            case "rain":
                setRainWeather(level);
                break;
            case "thunder":
                setThunderWeather(level);
                break;
            default:
                source.sendFailure(Component.literal("Неверный тип погоды! Используйте clear, rain или thunder."));
                return 0;
        }

        return 1;
    }

    private static void setClearWeather(ServerLevel level) {
        // Устанавливаем ясную погоду (нет дождя или грозы)
        level.setWeatherParameters(0, 0, false, false);
    }

    private static void setRainWeather(ServerLevel level) {
        // Устанавливаем дождь (погода с дождем)
        level.setWeatherParameters(6000, 0, true, false);  // Параметры: продолжительность, дождь, гроза
    }

    private static void setThunderWeather(ServerLevel level) {
        // Устанавливаем грозу
        level.setWeatherParameters(6000, 1, true, true);  // Параметры: продолжительность, дождь, гроза
    }
}
