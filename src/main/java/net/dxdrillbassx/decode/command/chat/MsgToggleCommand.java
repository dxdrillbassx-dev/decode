package net.dxdrillbassx.decode.command.chat;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber
public class MsgToggleCommand {

    private static final Map<String, Boolean> msgToggleStatus = new HashMap<>();

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("msgtoggle")
                .then(Commands.argument("player", StringArgumentType.string())
                        .then(Commands.argument("state", StringArgumentType.word())
                                .executes(context -> {
                                    String playerName = StringArgumentType.getString(context, "player");
                                    String state = StringArgumentType.getString(context, "state");
                                    return executeMsgToggle(context.getSource(), playerName, state);
                                })
                        )
                )
                .executes(context -> executeMsgToggle(context.getSource(), null, null))
        );

        dispatcher.register(Commands.literal("emsgtoggle").redirect(dispatcher.getRoot().getChild("msgtoggle")));
    }

    private static int executeMsgToggle(CommandSourceStack source, String playerName, String state) {
        MinecraftServer server = source.getServer();
        ServerPlayer executor = (ServerPlayer) source.getEntity();

        if (executor == null) {
            source.sendFailure(Component.literal("command.msgtoggle.only_players"));
            return 0;
        }

        if (playerName != null) {
            ServerPlayer targetPlayer = server.getPlayerList().getPlayerByName(playerName);
            if (targetPlayer == null) {
                source.sendFailure(Component.literal("command.msgtoggle.player_not_found"));
                return 0;
            }

            if (state == null) {
                source.sendFailure(Component.literal("command.msgtoggle.specify_state"));
                return 0;
            }

            if (state.equalsIgnoreCase("on")) {
                msgToggleStatus.put(playerName, true);
                targetPlayer.sendSystemMessage(Component.literal(playerName + " command.msgtoggle.blocked"));
            } else if (state.equalsIgnoreCase("off")) {
                msgToggleStatus.put(playerName, false);
                targetPlayer.sendSystemMessage(Component.literal(playerName + " command.msgtoggle.unblocked"));
            } else {
                source.sendFailure(Component.literal("command.msgtoggle.invalid_state"));
                return 0;
            }
        } else {
            boolean currentStatus = msgToggleStatus.getOrDefault(executor.getName().getString(), false);
            if (currentStatus) {
                msgToggleStatus.put(executor.getName().getString(), false);
                executor.sendSystemMessage(Component.literal("command.msgtoggle.unblocked_self"));
            } else {
                msgToggleStatus.put(executor.getName().getString(), true);
                executor.sendSystemMessage(Component.literal("command.msgtoggle.blocked_self"));
            }
        }

        return 1;
    }

}
