package net.dxdrillbassx.decode.command.player;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class HealCommand {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("heal")
                .then(Commands.argument("player", StringArgumentType.string())
                        .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                .executes(context -> {
                                    String playerName = StringArgumentType.getString(context, "player");
                                    int amount = IntegerArgumentType.getInteger(context, "amount");
                                    return executeHeal(context.getSource(), playerName, amount);
                                })
                        )
                        .executes(context -> {
                            String playerName = StringArgumentType.getString(context, "player");
                            return executeHeal(context.getSource(), playerName, -1);  // Если количество не указано, восстанавливаем полное здоровье
                        })
                )
                .executes(context -> {
                    return executeHeal(context.getSource(), null, -1);  // Лечим себя
                })
        );
    }

    private static int executeHeal(CommandSourceStack source, String playerName, int amount) {
        ServerPlayer player;

        if (playerName != null) {
            // Получаем игрока по имени
            player = source.getServer().getPlayerList().getPlayerByName(playerName);
            if (player == null) {
                source.sendFailure(Component.literal("Игрок с таким именем не найден!"));
                return 0;
            }
        } else {
            if (!(source.getEntity() instanceof ServerPlayer)) {
                source.sendFailure(Component.literal("Эту команду может использовать только игрок!"));
                return 0;
            }
            player = (ServerPlayer) source.getEntity();
        }

        // Лечим игрока
        if (amount == -1) {
            // Восстанавливаем полное здоровье
            player.setHealth(player.getMaxHealth());
            source.sendSuccess(Component.literal("Здоровье " + player.getName().getString() + " полностью восстановлено!"), true);
        } else {
            // Восстанавливаем заданное количество здоровья
            player.heal(amount);
            source.sendSuccess(Component.literal("Здоровье " + player.getName().getString() + " восстановлено на " + amount + " единиц."), true);
        }

        return 1;
    }
}
