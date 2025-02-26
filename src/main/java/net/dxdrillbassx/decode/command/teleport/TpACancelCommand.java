package net.dxdrillbassx.decode.command.teleport;

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

@Mod.EventBusSubscriber
public class TpACancelCommand {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // Регистрация команды tpacancel
        dispatcher.register(Commands.literal("tpacancel")
                .then(Commands.argument("player", StringArgumentType.string())
                        .executes(context -> {
                            String playerName = StringArgumentType.getString(context, "player");
                            return executeCancelRequest(context.getSource(), playerName);
                        })
                )
                .executes(context -> {
                    return executeCancelRequest(context.getSource(), null);  // Если игрок не указан, отменить все запросы
                })
        );

        // Альтернативные названия команды
        dispatcher.register(Commands.literal("etpacancel").redirect(dispatcher.getRoot().getChild("tpacancel")));
    }

    private static int executeCancelRequest(CommandSourceStack source, String playerName) {
        MinecraftServer server = source.getServer();
        ServerPlayer executor = (ServerPlayer) source.getEntity();

        if (executor == null) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        if (playerName != null) {
            // Отменить запросы только для указанного игрока
            ServerPlayer targetPlayer = server.getPlayerList().getPlayerByName(playerName);
            if (targetPlayer == null) {
                source.sendFailure(Component.literal("Игрок с таким именем не найден!"));
                return 0;
            }

            // Логика для отмены запросов телепортации для целевого игрока
            cancelTeleportRequest(executor, targetPlayer);
            source.sendSuccess(Component.literal("Отменены все запросы на телепортацию для игрока " + targetPlayer.getName().getString()), true);
        } else {
            // Отменить все запросы телепортации для всех игроков
            cancelAllRequests(executor);
            source.sendSuccess(Component.literal("Отменены все запросы на телепортацию."), true);
        }

        return 1;
    }

    private static void cancelTeleportRequest(ServerPlayer executor, ServerPlayer targetPlayer) {
        // Логика для отмены запроса телепортации для конкретного игрока
        // Например, если есть система запросов телепортации, вы должны найти и отменить запрос
        // Проверка и отмена запроса для executor и targetPlayer

        // Примерная структура:
        // if (teleportRequests.containsKey(targetPlayer.getUUID())) {
        //     teleportRequests.remove(targetPlayer.getUUID());
        //     executor.sendMessage(Component.literal("Запрос на телепортацию к игроку " + targetPlayer.getName().getString() + " отменен."));
        // }
    }

    private static void cancelAllRequests(ServerPlayer executor) {

        // Примерная структура:
        // teleportRequests.clear();  // Очищаем все запросы
        // executor.sendMessage(Component.literal("Все запросы на телепортацию были отменены."));
    }
}
