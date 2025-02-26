package net.dxdrillbassx.decode.command.admin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class KickCommand {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // Регистрация команды /kick
        dispatcher.register(Commands.literal("kick")
                .then(Commands.argument("player", StringArgumentType.string()) // Аргумент для имени игрока
                        .then(Commands.argument("reason", StringArgumentType.string()) // Аргумент для причины
                                .executes(context -> {
                                    String playerName = StringArgumentType.getString(context, "player");
                                    String reason = StringArgumentType.getString(context, "reason");
                                    return executeKickCommand(context.getSource(), playerName, reason);
                                })
                        )
                )
        );

        // Альтернативное название команды
        dispatcher.register(Commands.literal("ekick").redirect(dispatcher.getRoot().getChild("kick")));
    }

    private static int executeKickCommand(CommandSourceStack source, String playerName, String reason) {
        // Получаем игрока по имени
        ServerPlayer player = source.getServer().getPlayerList().getPlayerByName(playerName);
        if (player == null) {
            source.sendFailure(Component.literal("Игрок с именем " + playerName + " не найден."));
            return 0;
        }

        // Выкидываем игрока с причиной
        player.connection.disconnect(Component.literal("Вы были выкинуты с сервера. Причина: " + reason));
        source.sendSuccess(Component.literal("Игрок " + playerName + " был выкинут. Причина: " + reason), false);
        return 1;
    }
}
