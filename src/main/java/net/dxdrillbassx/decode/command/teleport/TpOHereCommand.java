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
public class TpOHereCommand {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // Регистрация команды tpohere
        dispatcher.register(Commands.literal("tpohere")
                .then(Commands.argument("player", StringArgumentType.string())
                        .executes(context -> {
                            String playerName = StringArgumentType.getString(context, "player");
                            return executeTeleportHere(context.getSource(), playerName);
                        })
                )
                .executes(context -> {
                    return executeTeleportHere(context.getSource(), null);  // Если игрок не указан, телепортировать самого себя
                })
        );

        // Альтернативные названия команды
        dispatcher.register(Commands.literal("etpohere").redirect(dispatcher.getRoot().getChild("tpohere")));
    }

    private static int executeTeleportHere(CommandSourceStack source, String playerName) {
        MinecraftServer server = source.getServer();
        ServerPlayer executor = (ServerPlayer) source.getEntity();

        if (executor == null) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        if (playerName != null) {
            // Телепортируем указанного игрока к себе
            ServerPlayer targetPlayer = server.getPlayerList().getPlayerByName(playerName);
            if (targetPlayer == null) {
                source.sendFailure(Component.literal("Игрок с таким именем не найден!"));
                return 0;
            }

            // Проверка tptoggle (если телепортация запрещена)
            if (isTeleportBlocked(targetPlayer)) {
                source.sendFailure(Component.literal("Телепортация для игрока " + playerName + " заблокирована."));
                return 0;
            }

            // Выполнение телепортации
            targetPlayer.teleportTo(executor.getX(), executor.getY(), executor.getZ());
            executor.sendSystemMessage(Component.literal("Игрок " + targetPlayer.getName().getString() + " был телепортирован к вам."));
            targetPlayer.sendSystemMessage(Component.literal("Вы были телепортированы к игроку " + executor.getName().getString() + "."));
        } else {
            // Если имя игрока не указано, телепортируем самого себя
            executor.sendSystemMessage(Component.literal("Вы уже здесь."));
        }

        return 1;
    }

    private static boolean isTeleportBlocked(ServerPlayer player) {
        // Логика для проверки, заблокирована ли телепортация для игрока
        // В примере проверяется некая система блокировки с помощью tptoggle, но вы можете использовать свою систему
        // Пример:
        // return teleportBlockList.contains(player.getUUID());
        return false; // Заглушка, пока не реализовано
    }
}
