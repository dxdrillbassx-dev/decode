package net.dxdrillbassx.decode.command.teleport;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;

@Mod.EventBusSubscriber
public class TPAllCommand {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("tpall")
                .then(Commands.argument("player", StringArgumentType.string())
                        .executes(context -> {
                            String playerName = StringArgumentType.getString(context, "player");
                            return executeTpAll(context.getSource(), playerName);
                        }))
        );
    }

    private static int executeTpAll(CommandSourceStack source, String playerName) {
        ServerPlayer targetPlayer = source.getServer().getPlayerList().getPlayerByName(playerName);
        if (targetPlayer == null) {
            source.sendFailure(Component.literal("Игрок с таким именем не найден!"));
            return 0;
        }

        Collection<ServerPlayer> players = source.getServer().getPlayerList().getPlayers();
        for (ServerPlayer player : players) {
            if (player != targetPlayer) {
                player.teleportTo(targetPlayer.getX(), targetPlayer.getY(), targetPlayer.getZ());
                source.sendSuccess(Component.literal("Телепортация игрока " + player.getName().getString() + " к " + targetPlayer.getName().getString() + "."), false);
            }
        }

        return 1;
    }
}
