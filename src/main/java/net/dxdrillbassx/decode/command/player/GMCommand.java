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

        // Регистрируем команду для различных гейм-модов и их алиасов
        registerGameModeCommands(dispatcher);
    }

    private static void registerGameModeCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        // Регистрируем команду для режима выживания и его алиасы
        dispatcher.register(Commands.literal("survival")
                .executes(context -> setGameMode(context.getSource(), GameType.SURVIVAL, "Survival"))
        );
        dispatcher.register(Commands.literal("esurvival")
                .executes(context -> setGameMode(context.getSource(), GameType.SURVIVAL, "Survival"))
        );
        dispatcher.register(Commands.literal("survivalmode")
                .executes(context -> setGameMode(context.getSource(), GameType.SURVIVAL, "Survival"))
        );
        dispatcher.register(Commands.literal("gms")
                .executes(context -> setGameMode(context.getSource(), GameType.SURVIVAL, "Survival"))
        );

        // Регистрируем команду для творческого режима и его алиасы
        dispatcher.register(Commands.literal("creative")
                .executes(context -> setGameMode(context.getSource(), GameType.CREATIVE, "Creative"))
        );
        dispatcher.register(Commands.literal("eecreative")
                .executes(context -> setGameMode(context.getSource(), GameType.CREATIVE, "Creative"))
        );
        dispatcher.register(Commands.literal("creativemode")
                .executes(context -> setGameMode(context.getSource(), GameType.CREATIVE, "Creative"))
        );
        dispatcher.register(Commands.literal("gmc")
                .executes(context -> setGameMode(context.getSource(), GameType.CREATIVE, "Creative"))
        );

        // Регистрируем команду для приключенческого режима и его алиасы
        dispatcher.register(Commands.literal("adventure")
                .executes(context -> setGameMode(context.getSource(), GameType.ADVENTURE, "Adventure"))
        );
        dispatcher.register(Commands.literal("eadventure")
                .executes(context -> setGameMode(context.getSource(), GameType.ADVENTURE, "Adventure"))
        );
        dispatcher.register(Commands.literal("adventuremode")
                .executes(context -> setGameMode(context.getSource(), GameType.ADVENTURE, "Adventure"))
        );
        dispatcher.register(Commands.literal("gma")
                .executes(context -> setGameMode(context.getSource(), GameType.ADVENTURE, "Adventure"))
        );

        // Регистрируем команду для режима наблюдателя и его алиасы
        dispatcher.register(Commands.literal("spectator")
                .executes(context -> setGameMode(context.getSource(), GameType.SPECTATOR, "Spectator"))
        );
        dispatcher.register(Commands.literal("espectator")
                .executes(context -> setGameMode(context.getSource(), GameType.SPECTATOR, "Spectator"))
        );
        dispatcher.register(Commands.literal("spec")
                .executes(context -> setGameMode(context.getSource(), GameType.SPECTATOR, "Spectator"))
        );
        dispatcher.register(Commands.literal("gmsp")
                .executes(context -> setGameMode(context.getSource(), GameType.SPECTATOR, "Spectator"))
        );
    }

    private static int setGameMode(CommandSourceStack source, GameType gameMode, String modeName) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        // Устанавливаем выбранный гейм-мод через player
        player.setGameMode(gameMode);

        // Уведомляем игрока об изменении гейм-мода
        source.sendSuccess(Component.literal("Гейм-мод изменен на: " + modeName), true);
        return 1;
    }
}
