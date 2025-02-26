package net.dxdrillbassx.decode.command.admin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class KickAllCommand {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("kickall")
                .then(Commands.argument("reason", StringArgumentType.string())
                        .executes(context -> {
                            String reason = StringArgumentType.getString(context, "reason");
                            return executeKickAllCommand(context.getSource(), reason);
                        })
                )
        );

        dispatcher.register(Commands.literal("ekickall").redirect(dispatcher.getRoot().getChild("kickall")));
    }

    private static int executeKickAllCommand(CommandSourceStack source, String reason) throws CommandSyntaxException {
        MinecraftServer server = source.getServer();

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player != source.getPlayerOrException()) {
                player.connection.disconnect(Component.translatable("command.kickall.kicked", reason));
            }
        }

        source.sendSuccess(Component.translatable("command.kickall.success", reason), false);
        return 1;
    }
}
