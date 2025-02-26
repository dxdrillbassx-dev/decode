package net.dxdrillbassx.decode.command.teleport;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class SetTprCommand {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("settpr")
                .then(Commands.argument("center", StringArgumentType.string())
                        .then(Commands.argument("minrange", IntegerArgumentType.integer())
                                .then(Commands.argument("maxrange", IntegerArgumentType.integer())
                                        .then(Commands.argument("value", IntegerArgumentType.integer())
                                                .executes(context -> {
                                                    String center = StringArgumentType.getString(context, "center");
                                                    int minRange = IntegerArgumentType.getInteger(context, "minrange");
                                                    int maxRange = IntegerArgumentType.getInteger(context, "maxrange");
                                                    int value = IntegerArgumentType.getInteger(context, "value");
                                                    return setTpr(context.getSource(), center, minRange, maxRange, value);
                                                })
                                        )
                                )
                        )
                )
        );
    }

    private static int setTpr(CommandSourceStack source, String center, int minRange, int maxRange, int value) {
        if (!(source.getEntity() instanceof ServerPlayer)) {
            source.sendFailure(Component.literal("Эту команду может использовать только игрок!"));
            return 0;
        }

        // Параметры для случайной телепортации
        // Логика установки случайного местоположения телепортации
        // Это может быть связано с координатами в мире или какой-то другой логикой
        // На данном этапе, например, логируем параметры

        source.sendSuccess(Component.literal("Настройки для случайного телепорта установлены:" +
                "\nЦентр: " + center +
                "\nМинимальное расстояние: " + minRange +
                "\nМаксимальное расстояние: " + maxRange +
                "\nЗначение: " + value), false);

        // Здесь может быть логика для сохранения этих параметров в конфиг
        // или их дальнейшее использование для телепортации

        return 1;
    }
}
