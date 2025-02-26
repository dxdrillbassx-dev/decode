package net.dxdrillbassx.decode.command.admin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class MoreCommand {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // Регистрация команды /more
        dispatcher.register(Commands.literal("more")
                .then(Commands.argument("amount", IntegerArgumentType.integer(1, 64)) // Указание максимума 64 (максимум для стеков предметов)
                        .executes(context -> {
                            int amount = IntegerArgumentType.getInteger(context, "amount");
                            return executeMoreCommand(context.getSource(), amount);
                        })
                )
                .executes(context -> {
                    return executeMoreCommand(context.getSource(), -1); // Если количество не указано, заполняем до максимума
                })
        );

        // Альтернативное название команды
        dispatcher.register(Commands.literal("emore").redirect(dispatcher.getRoot().getChild("more")));
    }

    private static int executeMoreCommand(CommandSourceStack source, int amount) {
        // Проверяем, что это игрок
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        // Получаем предмет в руке
        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.isEmpty()) {
            source.sendFailure(Component.literal("Вы должны держать предмет в руке!"));
            return 0;
        }

        // Если не указано количество, заполняем до максимума (64)
        if (amount == -1) {
            amount = heldItem.getMaxStackSize();
        }

        // Добавляем в инвентарь игрока
        int addAmount = Math.min(amount, heldItem.getMaxStackSize()) - heldItem.getCount(); // Количество для добавления
        if (addAmount > 0) {
            heldItem.grow(addAmount); // Увеличиваем количество предметов в руке
            source.sendSuccess(Component.literal("Предмет в руке был заполнен до " + heldItem.getCount() + " штук."), false);
        } else {
            source.sendFailure(Component.literal("У вас уже максимальное количество предметов в руке."));
            return 0;
        }

        return 1;
    }
}
