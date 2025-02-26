package net.dxdrillbassx.decode.command.teleport;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class TPAAllCommand {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("tpaall")
                .then(Commands.argument("player", StringArgumentType.string())
                        .executes(context -> {
                            String playerName = StringArgumentType.getString(context, "player");
                            return executeTPAAll(context.getSource(), playerName);
                        })
                )
        );

        // Альтернативные названия
        dispatcher.register(Commands.literal("etpaall").redirect(dispatcher.getRoot().getChild("tpaall")));
    }

    private static int executeTPAAll(CommandSourceStack source, String playerName) {
        Player executor = (Player) source.getEntity();
        if (executor == null) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        // Получаем игрока, который инициирует команду
        ServerPlayer targetPlayer = source.getServer().getPlayerList().getPlayerByName(playerName);
        if (targetPlayer == null) {
            source.sendFailure(Component.literal("Игрок с таким именем не найден!"));
            return 0;
        }

        // Оповещаем всех игроков на сервере
        for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
            if (!player.getName().getString().equals(targetPlayer.getName().getString())) {
                // Используем sendSystemMessage для отправки сообщений игрокам
                player.sendSystemMessage(Component.literal("Игрок " + targetPlayer.getName().getString() + " запросил телепортацию всех игроков к себе!"));
            }
        }

        // Выводим сообщение инициатору
        source.sendSuccess(Component.literal("Запрос на телепортацию был отправлен всем игрокам!"), true);

        return 1;
    }
}
