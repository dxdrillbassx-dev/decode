package net.dxdrillbassx.decode.command.player;

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
public class GetPosCommand {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // Регистрация команды getpos
        dispatcher.register(Commands.literal("getpos")
                .then(Commands.argument("player", StringArgumentType.string())
                        .executes(context -> {
                            String playerName = StringArgumentType.getString(context, "player");
                            return executeGetPos(context.getSource(), playerName);
                        })
                )
                .executes(context -> {
                    // Если игрок не указан, получаем координаты самого себя
                    return executeGetPos(context.getSource(), null);
                })
        );

        // Альтернативные названия команды
        dispatcher.register(Commands.literal("eposition").redirect(dispatcher.getRoot().getChild("getpos")));
    }

    private static int executeGetPos(CommandSourceStack source, String playerName) {
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

            // Получаем координаты указанного игрока
            double x = targetPlayer.getX();
            double y = targetPlayer.getY();
            double z = targetPlayer.getZ();

            // Отправляем сообщение с координатами
            executor.sendSystemMessage(Component.literal(playerName + " находится в координатах: X=" + x + ", Y=" + y + ", Z=" + z));
        } else {
            // Если игрок не указан, выводим координаты самого себя
            double x = executor.getX();
            double y = executor.getY();
            double z = executor.getZ();

            // Отправляем сообщение с координатами
            executor.sendSystemMessage(Component.literal("Вы находитесь в координатах: X=" + x + ", Y=" + y + ", Z=" + z));
        }

        return 1;
    }
}
