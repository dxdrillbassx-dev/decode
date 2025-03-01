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

import java.util.Map;

@Mod.EventBusSubscriber
public class ReplyCommand {
    private static final Map<ServerPlayer, String> lastMessagedPlayer = MsgCommand.lastMessagedPlayer;

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // Регистрируем команду /reply
        dispatcher.register(Commands.literal("reply")
                .then(Commands.argument("message", StringArgumentType.string())
                        .executes(context -> {
                            String message = StringArgumentType.getString(context, "message");
                            return replyToMessage(context.getSource(), message);
                        })
                )
        );
    }

    private static int replyToMessage(CommandSourceStack source, String message) {
        if (!(source.getEntity() instanceof ServerPlayer sender)) {
            source.sendFailure(Component.translatable("command.reply.only_players"));
            return 0;
        }

        // Проверяем, был ли получен последний игрок для ответа
        String lastPlayerName = lastMessagedPlayer.get(sender);
        if (lastPlayerName == null) {
            source.sendFailure(Component.translatable("command.reply.no_messages"));
            return 0;
        }

        ServerPlayer receiver = source.getServer().getPlayerList().getPlayerByName(lastPlayerName);
        if (receiver == null) {
            source.sendFailure(Component.translatable("command.reply.player_not_found"));
            return 0;
        }

        // Отправляем сообщение
        receiver.sendSystemMessage(Component.translatable("command.reply.sender_message", sender.getName().getString(), message));
        sender.sendSystemMessage(Component.translatable("command.reply.receiver_message", message));

        return 1;
    }
}
