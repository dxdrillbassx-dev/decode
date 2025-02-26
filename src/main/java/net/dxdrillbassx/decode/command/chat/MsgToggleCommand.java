package net.dxdrillbassx.decode.command.chat;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber
public class MsgToggleCommand {

    // Хранение состояния для блокировки приватных сообщений для каждого игрока
    private static final Map<String, Boolean> msgToggleStatus = new HashMap<>();

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // Регистрация команды msgtoggle
        dispatcher.register(Commands.literal("msgtoggle")
                .then(Commands.argument("player", StringArgumentType.string())
                        .then(Commands.argument("state", StringArgumentType.word())
                                .executes(context -> {
                                    String playerName = StringArgumentType.getString(context, "player");
                                    String state = StringArgumentType.getString(context, "state");
                                    return executeMsgToggle(context.getSource(), playerName, state);
                                })
                        )
                )
                .executes(context -> {
                    // Если игрок не указан, то применяем команду к себе
                    return executeMsgToggle(context.getSource(), null, null);
                })
        );

        // Альтернативные названия команды
        dispatcher.register(Commands.literal("emsgtoggle").redirect(dispatcher.getRoot().getChild("msgtoggle")));
    }

    private static int executeMsgToggle(CommandSourceStack source, String playerName, String state) {
        MinecraftServer server = source.getServer();
        ServerPlayer executor = (ServerPlayer) source.getEntity();

        if (executor == null) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        if (playerName != null) {
            // Получаем указанного игрока
            ServerPlayer targetPlayer = server.getPlayerList().getPlayerByName(playerName);
            if (targetPlayer == null) {
                source.sendFailure(Component.literal("Игрок с таким именем не найден!"));
                return 0;
            }

            if (state == null) {
                source.sendFailure(Component.literal("Укажите состояние: on или off!"));
                return 0;
            }

            // Переключаем статус блокировки сообщений для указанного игрока
            if (state.equalsIgnoreCase("on")) {
                msgToggleStatus.put(playerName, true);
                targetPlayer.sendSystemMessage(Component.literal(playerName + " заблокировал все приватные сообщения."));
            } else if (state.equalsIgnoreCase("off")) {
                msgToggleStatus.put(playerName, false);
                targetPlayer.sendSystemMessage(Component.literal(playerName + " разблокировал приватные сообщения."));
            } else {
                source.sendFailure(Component.literal("Неверное состояние. Укажите 'on' или 'off'."));
                return 0;
            }
        } else {
            // Переключаем статус блокировки сообщений для себя
            boolean currentStatus = msgToggleStatus.getOrDefault(executor.getName().getString(), false);
            if (currentStatus) {
                msgToggleStatus.put(executor.getName().getString(), false);
                executor.sendSystemMessage(Component.literal("Вы теперь можете получать приватные сообщения."));
            } else {
                msgToggleStatus.put(executor.getName().getString(), true);
                executor.sendSystemMessage(Component.literal("Вы заблокировали получение приватных сообщений."));
            }
        }

        return 1;
    }

    // Получить статус блокировки приватных сообщений для игрока
    public static boolean isMsgBlocked(String playerName) {
        return msgToggleStatus.getOrDefault(playerName, false);
    }
}
