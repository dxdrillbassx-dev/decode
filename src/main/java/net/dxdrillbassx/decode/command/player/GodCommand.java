package net.dxdrillbassx.decode.command.player;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.UUID;

@Mod.EventBusSubscriber
public class GodCommand {
    private static final HashSet<UUID> godModePlayers = new HashSet<>();

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("god")
                .executes(context -> toggleGodMode(context.getSource())));
    }

    private static int toggleGodMode(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.translatable("command.god.only_players"));
            return 0;
        }

        // Проверка прав игрока (например, администратор)
        if (!source.hasPermission(2)) { // Уровень 2 — администратор
            source.sendFailure(Component.translatable("command.god.no_permission"));
            return 0;
        }

        UUID playerId = player.getUUID();

        if (godModePlayers.contains(playerId)) {
            godModePlayers.remove(playerId);
            player.setInvulnerable(false);
            player.sendSystemMessage(Component.translatable("command.god.disabled"));
        } else {
            godModePlayers.add(playerId);
            player.setInvulnerable(true);
            player.sendSystemMessage(Component.translatable("command.god.enabled"));
        }

        return 1;
    }
}
