package net.dxdrillbassx.decode.command.chat;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber
public class MsgCommand {
    static final Map<ServerPlayer, String> lastMessagedPlayer = new HashMap<>();

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // Регистрируем команду /msg
        dispatcher.register(Commands.literal("msg")
                .then(Commands.argument("player", StringArgumentType.string())
                        .then(Commands.argument("message", StringArgumentType.string())
                                .executes(context -> {
                                    String playerName = StringArgumentType.getString(context, "player");
                                    String message = StringArgumentType.getString(context, "message");
                                    return sendMessage(context.getSource(), playerName, message);
                                }))
                )
        );
    }

    private static int sendMessage(CommandSourceStack source, String playerName, String message) {
        if (!(source.getEntity() instanceof ServerPlayer sender)) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        // Получаем игрока по имени
        ServerPlayer receiver = source.getServer().getPlayerList().getPlayerByName(playerName);
        if (receiver == null) {
            source.sendFailure(Component.literal("Игрок с таким именем не найден!"));
            return 0;
        }

        // Сохраняем, кто последний отправил сообщение
        lastMessagedPlayer.put(sender, receiver.getName().getString());

        // Отправляем сообщение
        receiver.sendSystemMessage(Component.literal(sender.getName().getString() + " -> " + message));
        sender.sendSystemMessage(Component.literal("Вы -> " + message));

        return 1;
    }
}
