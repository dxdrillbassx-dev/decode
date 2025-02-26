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

@Mod.EventBusSubscriber
public class TPToggleCommand {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("tptoggle")
                .then(Commands.argument("player", StringArgumentType.string())
                        .then(Commands.argument("state", StringArgumentType.string())
                                .suggests((context, builder) -> {
                                    builder.suggest("on");
                                    builder.suggest("off");
                                    return builder.buildFuture();
                                })
                                .executes(context -> {
                                    String playerName = StringArgumentType.getString(context, "player");
                                    String state = StringArgumentType.getString(context, "state");
                                    return executeTPToggle(context.getSource(), playerName, state);
                                })
                        )
                )
        );
    }

    private static int executeTPToggle(CommandSourceStack source, String playerName, String state) {
        ServerPlayer player = source.getServer().getPlayerList().getPlayerByName(playerName);
        if (player == null) {
            source.sendFailure(Component.literal("Игрок с таким именем не найден!"));
            return 0;
        }

        if ("on".equalsIgnoreCase(state)) {
            // Блокируем телепортацию для игрока
            player.getPersistentData().putBoolean("canTeleport", false);
            source.sendSuccess(Component.literal("Телепортация для игрока " + player.getName().getString() + " заблокирована."), false);
        } else if ("off".equalsIgnoreCase(state)) {
            // Разрешаем телепортацию для игрока
            player.getPersistentData().putBoolean("canTeleport", true);
            source.sendSuccess(Component.literal("Телепортация для игрока " + player.getName().getString() + " разрешена."), false);
        } else {
            source.sendFailure(Component.literal("Используйте /tptoggle [игрок] [on|off]"));
            return 0;
        }

        return 1;
    }
}
