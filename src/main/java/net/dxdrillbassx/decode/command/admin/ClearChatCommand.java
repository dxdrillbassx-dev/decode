package net.dxdrillbassx.decode.command.admin;

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
public class ClearChatCommand {
    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // Регистрируем команду /clear
        dispatcher.register(Commands.literal("clear")
                .executes(context -> clearChat(context.getSource()))
        );
    }

    private static int clearChat(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.translatable("command.clear.only_players"));
            return 0;
        }

        ServerLevel level = player.getLevel();

        // Отправка пустых сообщений для очистки чата
        for (int i = 0; i < 100; i++) {
            level.players().forEach(p -> p.sendSystemMessage(Component.literal(" ")));
        }

        player.sendSystemMessage(Component.translatable("command.clear.chat_cleared"));

        return 1;
    }
}
