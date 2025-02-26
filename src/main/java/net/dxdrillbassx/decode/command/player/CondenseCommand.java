package net.dxdrillbassx.decode.command.player;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CondenseCommand {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // Регистрируем команду /condense и ее алиасы
        dispatcher.register(Commands.literal("condense")
                .then(Commands.argument("itemname", StringArgumentType.string())
                        .executes(context -> condenseItems(context.getSource(), StringArgumentType.getString(context, "itemname")))
                )
        );

        // Алиасы для команды /condense
        dispatcher.register(Commands.literal("econdense")
                .then(Commands.argument("itemname", StringArgumentType.string())
                        .executes(context -> condenseItems(context.getSource(), StringArgumentType.getString(context, "itemname")))
                )
        );
        dispatcher.register(Commands.literal("compact")
                .then(Commands.argument("itemname", StringArgumentType.string())
                        .executes(context -> condenseItems(context.getSource(), StringArgumentType.getString(context, "itemname")))
                )
        );
        dispatcher.register(Commands.literal("ecompact")
                .then(Commands.argument("itemname", StringArgumentType.string())
                        .executes(context -> condenseItems(context.getSource(), StringArgumentType.getString(context, "itemname")))
                )
        );
        dispatcher.register(Commands.literal("blocks")
                .then(Commands.argument("itemname", StringArgumentType.string())
                        .executes(context -> condenseItems(context.getSource(), StringArgumentType.getString(context, "itemname")))
                )
        );
        dispatcher.register(Commands.literal("eblocks")
                .then(Commands.argument("itemname", StringArgumentType.string())
                        .executes(context -> condenseItems(context.getSource(), StringArgumentType.getString(context, "itemname")))
                )
        );
        dispatcher.register(Commands.literal("toblocks")
                .then(Commands.argument("itemname", StringArgumentType.string())
                        .executes(context -> condenseItems(context.getSource(), StringArgumentType.getString(context, "itemname")))
                )
        );
        dispatcher.register(Commands.literal("etoblocks")
                .then(Commands.argument("itemname", StringArgumentType.string())
                        .executes(context -> condenseItems(context.getSource(), StringArgumentType.getString(context, "itemname")))
                )
        );
    }

    private static int condenseItems(CommandSourceStack source, String itemName) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.translatable("command.condense.only_players"));
            return 0;
        }

        // Преобразование строки itemName в соответствующий предмет
        Item item = getItemFromName(itemName);
        if (item == null) {
            source.sendFailure(Component.translatable("command.condense.item_not_found", itemName));
            return 0;
        }

        // Проверка, если предмет является предметом, который можно конденсировать в блок
        if (item instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            if (block == Blocks.AIR) {
                source.sendFailure(Component.translatable("command.condense.cannot_condense"));
                return 0;
            }

            // Создаем ItemStack для блока и добавляем его в инвентарь игрока
            ItemStack itemStack = new ItemStack(blockItem);
            if (!player.getInventory().add(itemStack)) {
                source.sendFailure(Component.translatable("command.condense.add_block_failed"));
                return 0;
            }

            source.sendSuccess(Component.translatable("command.condense.item_condensed", itemName), true);
            return 1;
        } else {
            source.sendFailure(Component.translatable("command.condense.item_is_not_block"));
            return 0;
        }
    }

    private static Item getItemFromName(String name) {
        // В этом методе вы можете настроить преобразование имени в соответствующий предмет.
        return switch (name.toLowerCase()) {
            case "iron_ingot" -> Items.IRON_INGOT;
            case "gold_ingot" -> Items.GOLD_INGOT;
            case "diamond" -> Items.DIAMOND;
            // Добавьте сюда другие предметы по вашему усмотрению
            default -> null;
        };
    }
}
