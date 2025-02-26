package net.dxdrillbassx.decode.command.player;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class LevelCommand {
    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // Регистрируем команду /level
        dispatcher.register(Commands.literal("level")
                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                        .executes(context -> levelUpPlayer(context.getSource(), IntegerArgumentType.getInteger(context, "amount"))))
        );

        // Альтернативные названия
        dispatcher.register(Commands.literal("elevel").redirect(dispatcher.getRoot().getChild("level")));
    }

    private static int levelUpPlayer(CommandSourceStack source, int amount) {
        // Проверяем, является ли командующий игроком
        if (!(source.getEntity() instanceof Player player)) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        // Увеличиваем уровень игрока
        player.giveExperienceLevels(amount);

        source.sendSuccess(Component.literal("Игрок " + player.getName().getString() + " повысил уровень на " + amount + "!"), true);

        return 1;
    }
}
