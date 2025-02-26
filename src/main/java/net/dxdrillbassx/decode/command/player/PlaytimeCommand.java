package net.dxdrillbassx.decode.command.player;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class PlaytimeCommand {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("playtime")
                .then(Commands.argument("player", StringArgumentType.string())
                        .executes(context -> {
                            String playerName = StringArgumentType.getString(context, "player");
                            return executePlaytime(context.getSource(), playerName);
                        }))
                .executes(context -> executePlaytime(context.getSource(), null))
        );
    }

    private static int executePlaytime(CommandSourceStack source, String playerName) {
        ServerPlayer player;

        if (playerName != null) {
            // Получаем игрока по имени
            player = source.getServer().getPlayerList().getPlayerByName(playerName);
            if (player == null) {
                source.sendFailure(Component.literal("Игрок с таким именем не найден!"));
                return 0;
            }
        } else {
            if (!(source.getEntity() instanceof ServerPlayer)) {
                source.sendFailure(Component.literal("Эту команду может использовать только игрок!"));
                return 0;
            }
            player = (ServerPlayer) source.getEntity();
        }

        // Получаем время игры через Stats, используя правильный идентификатор статистики
        long playtime = player.getStats().getValue(Stats.CUSTOM.get(Stats.TIME_SINCE_DEATH));

        // Переводим время в минуты (в тиках)
        long playtimeInMinutes = playtime / 20 / 60;

        // Отправляем сообщение с временем
        source.sendSuccess(Component.literal("Игрок " + player.getName().getString() + " провел в игре: " + playtimeInMinutes + " минут."), false);

        return 1;
    }
}
