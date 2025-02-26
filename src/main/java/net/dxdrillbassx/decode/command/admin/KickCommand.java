package net.dxdrillbassx.decode.command.admin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class KickCommand {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("kick")
                .then(Commands.argument("player", StringArgumentType.string())
                        .then(Commands.argument("reason", StringArgumentType.string())
                                .executes(context -> {
                                    String playerName = StringArgumentType.getString(context, "player");
                                    String reason = StringArgumentType.getString(context, "reason");
                                    return executeKickCommand(context.getSource(), playerName, reason);
                                })
                        )
                )
        );

        dispatcher.register(Commands.literal("ekick").redirect(dispatcher.getRoot().getChild("kick")));
    }

    private static int executeKickCommand(CommandSourceStack source, String playerName, String reason) {
        ServerPlayer player = source.getServer().getPlayerList().getPlayerByName(playerName);
        if (player == null) {
            source.sendFailure(Component.translatable("command.kick.not_found", playerName));
            return 0;
        }

        player.connection.disconnect(Component.translatable("command.kick.kicked", reason));
        source.sendSuccess(Component.translatable("command.kick.success", playerName, reason), false);
        return 1;
    }
}
