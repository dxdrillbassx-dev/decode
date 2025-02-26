package net.dxdrillbassx.decode.command.player;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class SuicideCommand {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // Регистрация команды suicide
        dispatcher.register(Commands.literal("suicide")
                .executes(context -> {
                    return executeSuicideCommand(context.getSource());
                })
        );

        // Альтернативное название команды
        dispatcher.register(Commands.literal("esuicide").redirect(dispatcher.getRoot().getChild("suicide")));
    }

    private static int executeSuicideCommand(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();

        // Проверка на существование игрока
        if (player == null) {
            source.sendFailure(Component.literal("Не найден игрок для выполнения команды."));
            return 0;
        }

        // Устанавливаем здоровье игрока в 0, что приведет к его смерти
        player.setHealth(0);

        // Отправляем сообщение игроку о его смерти
        player.sendSystemMessage(Component.literal("Вы погибли."));

        // Отправляем сообщение всем игрокам о смерти этого игрока
        MinecraftServer server = player.getServer();
        if (server != null) {
            // Получаем командный источник и отправляем системное сообщение всем игрокам
            server.sendSystemMessage(Component.literal(player.getName().getString() + " покончил с собой."));
        }

        return 1;
    }
}
