package net.dxdrillbassx.decode.command.server;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;

@Mod.EventBusSubscriber
public class WhoisCommand {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // Регистрация команды whois
        dispatcher.register(Commands.literal("whois")
                .then(Commands.argument("playername", StringArgumentType.string())
                        .executes(context -> {
                            String playerName = StringArgumentType.getString(context, "playername");
                            return executeWhoisCommand(context.getSource(), playerName);
                        })
                )
        );

        // Альтернативное название команды
        dispatcher.register(Commands.literal("ewhois").redirect(dispatcher.getRoot().getChild("whois")));
    }

    private static int executeWhoisCommand(CommandSourceStack source, String playerName) {
        // Получаем игрока по имени
        MinecraftServer server = source.getServer();

        ServerPlayer targetPlayer = null;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player.getName().getString().equals(playerName)) {
                targetPlayer = player;
                break;
            }
        }

        if (targetPlayer == null) {
            source.sendFailure(Component.literal("Игрок с таким именем не найден."));
            return 0;
        }

        // Формируем информацию об игроке
        StringBuilder info = new StringBuilder();
        info.append("Информация о игроке ").append(targetPlayer.getName().getString()).append(":\n")
                .append("UUID: ").append(targetPlayer.getUUID()).append("\n")
                .append("Здоровье: ").append(targetPlayer.getHealth()).append("\n")
                .append("Уровень: ").append(targetPlayer.experienceLevel).append("\n");

        // Получаем игровой режим
        ServerPlayerGameMode gameMode = targetPlayer.gameMode;
        info.append("Игровой режим: ").append(gameMode).append("\n");

        // Получаем IP-адрес игрока (если доступно)
        try {
            String playerIp = Objects.requireNonNull(Objects.requireNonNull(targetPlayer.getServer()).getConnection()).getConnections().toString();
            info.append("IP-адрес: ").append(playerIp).append("\n");
        } catch (Exception e) {
            info.append("IP-адрес: Не доступен\n");
        }

        // Отправляем информацию
        source.sendSuccess(Component.literal(info.toString()), false);
        return 1;
    }
}
