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
            source.sendFailure(Component.translatable("command.clearinventory.only_players"));
            return 0;
        }

        // Очищаем инвентарь игрока
        player.getInventory().clearContent();
        player.sendSystemMessage(Component.translatable("command.clearinventory.inventory_cleared"));

        return 1;
    }

    private static int clearInventoryOfPlayer(CommandSourceStack source, String playerName) {
        // Проверяем, является ли командующий игроком
        if (!(source.getEntity() instanceof Player admin)) {
            source.sendFailure(Component.translatable("command.clearinventory.only_players"));
            return 0;
        }

        // Находим игрока по имени
        Player target = source.getServer().getPlayerList().getPlayerByName(playerName);
        if (target == null) {
            source.sendFailure(Component.translatable("command.clearinventory.player_not_found"));
            return 0;
        }

        // Очищаем инвентарь другого игрока
        target.getInventory().clearContent();
        target.sendSystemMessage(Component.translatable("command.clearinventory.inventory_cleared_admin", admin.getName().getString()));

        admin.sendSystemMessage(Component.translatable("command.clearinventory.inventory_cleared_target", playerName));

        return 1;
    }
}
