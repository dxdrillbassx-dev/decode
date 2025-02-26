package net.dxdrillbassx.decode.command.player;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class HatCommand {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // Регистрируем команду /hat
        dispatcher.register(Commands.literal("hat")
                .executes(context -> handleHatAction(context.getSource()))
        );

        // Алиас для команды /ehat
        dispatcher.register(Commands.literal("ehat")
                .executes(context -> handleHatAction(context.getSource()))
        );
    }

    private static int handleHatAction(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        // Получаем предмет, который игрок держит в руках
        ItemStack heldItem = player.getMainHandItem();

        // Проверяем, является ли этот предмет блоком
        if (heldItem.isEmpty() || !(heldItem.getItem() instanceof BlockItem)) {
            source.sendFailure(Component.literal("Держите блок в руках, чтобы использовать эту команду!"));
            return 0;
        }

        // Получаем сам блок из предмета
        Block block = ((BlockItem) heldItem.getItem()).getBlock();

        // Создаем новый ItemStack с этим блоком для головы
        ItemStack helmet = new ItemStack(block.asItem());

        // Надеваем предмет на голову игрока
        player.setItemSlot(EquipmentSlot.HEAD, helmet);

        // Сообщаем игроку, что шлем был одет
        source.sendSuccess(Component.literal("Вы надели " + block.getName() + " на голову!"), true);
        return 1;
    }
}
