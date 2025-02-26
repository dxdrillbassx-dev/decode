package net.dxdrillbassx.decode.command.chat;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ListCommand {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // Регистрируем команду /list
        dispatcher.register(Commands.literal("list")
                .executes(context -> listPlayers(context.getSource()))
        );
    }

    private static int listPlayers(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        // Получаем всех игроков на сервере
        ServerLevel level = player.getLevel();
        StringBuilder playerList = new StringBuilder("Игроки онлайн: ");
        level.players().forEach(p -> playerList.append(p.getName().getString()).append(", "));

        // Убираем последнюю запятую и пробел
        if (playerList.length() > 0) {
            playerList.setLength(playerList.length() - 2);
        }

        // Отправляем список игрокам
        source.sendSystemMessage(Component.literal(playerList.toString()));

        return 1;
    }
}
