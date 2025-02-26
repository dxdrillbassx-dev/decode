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
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber
public class TPOfflineCommand {

    // Хранение координат последних известных местоположений игроков
    private static final Map<String, double[]> playerLastLocation = new HashMap<>();

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("tpoffline")
                .then(Commands.argument("player", StringArgumentType.string())
                        .executes(context -> {
                            String playerName = StringArgumentType.getString(context, "player");
                            return executeTPOffline(context.getSource(), playerName);
                        })
                )
        );

        // Альтернативные названия
        dispatcher.register(Commands.literal("otp").redirect(dispatcher.getRoot().getChild("tpoffline")));
        dispatcher.register(Commands.literal("offlinetp").redirect(dispatcher.getRoot().getChild("tpoffline")));
        dispatcher.register(Commands.literal("tpoff").redirect(dispatcher.getRoot().getChild("tpoffline")));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        if (player != null) {
            double[] location = new double[] {
                    player.getX(),
                    player.getY(),
                    player.getZ()
            };
            playerLastLocation.put(player.getName().getString(), location);
        }
    }

    private static int executeTPOffline(CommandSourceStack source, String playerName) {
        double[] lastLocation = playerLastLocation.get(playerName);
        if (lastLocation == null) {
            source.sendFailure(Component.literal("Игрок с таким именем не найден или у него нет информации о последнем местоположении!"));
            return 0;
        }

        // Получаем игрока, который выполняет команду
        Player executor = (Player) source.getEntity();
        if (executor == null) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        // Телепортируем игрока
        executor.teleportTo(lastLocation[0], lastLocation[1], lastLocation[2]);
        source.sendSuccess(Component.literal("Телепортировали " + executor.getName().getString() + " к последнему месту выхода игрока " + playerName + "."), true);

        return 1;
    }
}
