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
        dispatcher.register(Commands.literal("list")
                .executes(context -> listPlayers(context.getSource()))
        );
    }

    private static int listPlayers(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.translatable("command.list.only_players"));
            return 0;
        }

        ServerLevel level = player.getLevel();
        StringBuilder playerList = new StringBuilder(Component.translatable("command.list.players_online").getString());

        level.players().forEach(p -> playerList.append(p.getName().getString()).append(", "));

        if (playerList.length() > 14) {
            playerList.setLength(playerList.length() - 2);
        } else {
            playerList.append(Component.translatable("command.list.no_players_online").getString());
        }

        source.sendSystemMessage(Component.literal(playerList.toString()));

        return 1;
    }
}
