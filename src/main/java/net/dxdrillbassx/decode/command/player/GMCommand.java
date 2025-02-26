package net.dxdrillbassx.decode.command.player;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class GMCommand {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // Регистрация команд для каждого режима
        registerGameModeCommand(dispatcher, "survival", GameType.SURVIVAL, "Survival");
        registerGameModeCommand(dispatcher, "creative", GameType.CREATIVE, "Creative");
        registerGameModeCommand(dispatcher, "adventure", GameType.ADVENTURE, "Adventure");
        registerGameModeCommand(dispatcher, "spectator", GameType.SPECTATOR, "Spectator");
    }

    private static void registerGameModeCommand(CommandDispatcher<CommandSourceStack> dispatcher, String commandName, GameType gameMode, String modeName) {
        // Регистрация команды и ее алиасов для каждого режима
        dispatcher.register(Commands.literal(commandName)
                .executes(context -> setGameMode(context.getSource(), gameMode, modeName))
        );
        dispatcher.register(Commands.literal("e" + commandName)
                .executes(context -> setGameMode(context.getSource(), gameMode, modeName))
        );
        dispatcher.register(Commands.literal(commandName + "mode")
                .executes(context -> setGameMode(context.getSource(), gameMode, modeName))
        );
        dispatcher.register(Commands.literal("gm" + commandName.charAt(0)) // сокращенный вариант
                .executes(context -> setGameMode(context.getSource(), gameMode, modeName))
        );
    }

    private static int setGameMode(CommandSourceStack source, GameType gameMode, String modeName) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.translatable("command.gm.only_players"));
            return 0;
        }

        // Устанавливаем выбранный гейм-мод
        player.setGameMode(gameMode);

        // Уведомляем игрока о смене гейм-мода
        source.sendSuccess(Component.translatable("command.gm.mode_changed", modeName), true);
        return 1;
    }
}
