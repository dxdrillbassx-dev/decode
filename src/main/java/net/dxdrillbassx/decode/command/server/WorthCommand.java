package net.dxdrillbassx.decode.command.server;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class WorthCommand {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // Регистрируем команду /worth
        dispatcher.register(Commands.literal("worth")
                .executes(context -> calculateWorth(context.getSource()))
        );

        // Алиас для команды /eworth
        dispatcher.register(Commands.literal("eworth")
                .executes(context -> calculateWorth(context.getSource()))
        );
    }

    private static int calculateWorth(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        // Получаем предмет в руке игрока
        ItemStack heldItem = player.getMainHandItem();

        // Проверяем, есть ли предмет в руке
        if (heldItem.isEmpty()) {
            source.sendFailure(Component.literal("Вы не держите предмет в руке!"));
            return 0;
        }

        // Рассчитываем стоимость предмета
        int worth = calculateItemWorth(heldItem);

        // Выводим стоимость предмета
        source.sendSuccess(Component.literal("Стоимость предмета " + heldItem.getDisplayName().getString() + ": " + worth + " монет."), true);
        return 1;
    }

    private static int calculateItemWorth(ItemStack itemStack) {
        Item item = itemStack.getItem();

        // Пример простого расчета стоимости предмета в зависимости от его типа
        if (item instanceof BlockItem) {
            // Стоимость блоков
            return 10; // Цена блока по умолчанию
        } else {
            // Пример для других типов предметов
            return 5; // Цена обычных предметов
        }
    }
}
