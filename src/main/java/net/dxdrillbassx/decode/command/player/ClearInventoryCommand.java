package net.dxdrillbassx.decode.command.player;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.mojang.brigadier.arguments.StringArgumentType;

@Mod.EventBusSubscriber
public class ClearInventoryCommand {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // Регистрируем команду /clearinventory
        dispatcher.register(Commands.literal("clearinventory")
                .executes(context -> clearInventory(context.getSource()))
        );

        // Регистрируем команду /clearinventory [игрок]
        dispatcher.register(Commands.literal("clearinventory")
                .then(Commands.argument("player", StringArgumentType.word())
                        .executes(context -> clearInventoryOfPlayer(context.getSource(), StringArgumentType.getString(context, "player")))
                )
        );
    }

    private static int clearInventory(CommandSourceStack source) {
        // Проверяем, является ли командующий игроком
        if (!(source.getEntity() instanceof Player player)) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        // Очищаем инвентарь игрока
        player.getInventory().clearContent();
        player.sendSystemMessage(Component.literal("Ваш инвентарь был очищен"));

        return 1;
    }

    private static int clearInventoryOfPlayer(CommandSourceStack source, String playerName) {
        // Проверяем, является ли командующий игроком
        if (!(source.getEntity() instanceof Player admin)) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        // Находим игрока по имени
        Player target = source.getServer().getPlayerList().getPlayerByName(playerName);
        if (target == null) {
            source.sendFailure(Component.literal("Игрок с таким именем не найден"));
            return 0;
        }

        // Очищаем инвентарь другого игрока
        target.getInventory().clearContent();
        target.sendSystemMessage(Component.literal("Ваш инвентарь был очищен администратором " + admin.getName().getString()));

        admin.sendSystemMessage(Component.literal("Инвентарь игрока " + playerName + " был очищен"));

        return 1;
    }
}
