package net.dxdrillbassx.decode.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.server.ServerStartingEvent;

public class AfkCommand {

    // Метод для регистрации команд
    public static void registerCommands(ServerStartingEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getServer().getCommands().getDispatcher();

        // Регистрация команды
        dispatcher.register(
                Commands.literal("afk")
                        .then(Commands.argument("message", StringArgumentType.greedyString())
                                .executes(ctx -> markAfk((CommandSource) ctx.getSource(), StringArgumentType.getString(ctx, "message"))))
                        .executes(ctx -> markAfk((CommandSource) ctx.getSource(), null))
        );

        // Алиасы для команды
        dispatcher.register(
                Commands.literal("eafk")
                        .executes(ctx -> markAfk((CommandSource) ctx.getSource(), null))
        );

        dispatcher.register(
                Commands.literal("away")
                        .executes(ctx -> markAfk((CommandSource) ctx.getSource(), null))
        );

        dispatcher.register(
                Commands.literal("eaway")
                        .executes(ctx -> markAfk((CommandSource) ctx.getSource(), null))
        );
    }

    private static int markAfk(CommandSource source, String message) {
        if (source instanceof ServerPlayer player) {
            String playerName = player.getName().getString();
            Component responseMessage;

            if (message == null || message.isEmpty()) {
                // Стандартное сообщение, если нет аргументов
                responseMessage = Component.literal(playerName + " теперь AFK");
            } else {
                // Сообщение, если аргумент message указан
                responseMessage = Component.literal(playerName + " теперь AFK: " + message);
            }

            // Отправляем сообщение в чат
            source.sendSystemMessage(responseMessage);

            // Логика для отслеживания AFK статуса игрока
            player.getTags().add("afk");

            return 1;
        }
        return 0;
    }
}
