package net.dxdrillbassx.decode.command.admin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class KickAllCommand {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // Регистрация команды kickall
        dispatcher.register(Commands.literal("kickall")
                .then(Commands.argument("reason", StringArgumentType.string())
                        .executes(context -> {
                            String reason = StringArgumentType.getString(context, "reason");
                            return executeKickAllCommand(context.getSource(), reason);
                        })
                )
        );

        // Альтернативные названия команды
        dispatcher.register(Commands.literal("ekickall").redirect(dispatcher.getRoot().getChild("kickall")));
    }

    private static int executeKickAllCommand(CommandSourceStack source, String reason) throws CommandSyntaxException {
        // Получаем сервер
        MinecraftServer server = source.getServer();
        if (server == null) {
            source.sendFailure(Component.literal("Сервер не найден."));
            return 0;
        }

        // Получаем список всех игроков
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            // Не выкидываем игрока, который выполнил команду
            if (player != source.getPlayerOrException()) {
                // Выкидываем игрока с сообщением о причине
                player.connection.disconnect(Component.literal("Вы были выкинуты с сервера. Причина: " + reason));
            }
        }

        source.sendSuccess(Component.literal("Все игроки (кроме вас) были выкинуты. Причина: " + reason), false);
        return 1;
    }
}
