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
public class TPAHereCommand {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("tpahere")
                .then(Commands.argument("player", StringArgumentType.string())
                        .executes(context -> {
                            String playerName = StringArgumentType.getString(context, "player");
                            return executeTPAHere(context.getSource(), playerName);
                        })
                )
        );

        // Альтернативные названия
        dispatcher.register(Commands.literal("etpahere").redirect(dispatcher.getRoot().getChild("tpahere")));
    }

    private static int executeTPAHere(CommandSourceStack source, String playerName) {
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

        // Проверяем, что цель не сама себя телепортирует
        if (targetPlayer.equals(executor)) {
            source.sendFailure(Component.literal("Вы не можете телепортировать себя к себе!"));
            return 0;
        }

        // Оповещаем игрока-инициатора
        executor.sendSystemMessage(Component.literal("Запрос на телепортацию игрока " + targetPlayer.getName().getString() + " к вам был отправлен!"));

        // Оповещаем целевого игрока
        targetPlayer.sendSystemMessage(Component.literal("Игрок " + executor.getName().getString() + " попросил вас телепортироваться к нему. Используйте /accept для принятия или /deny для отказа."));

        return 1;
    }
}
