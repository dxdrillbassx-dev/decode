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

        dispatcher.register(Commands.literal("more")
                .then(Commands.argument("amount", IntegerArgumentType.integer(1, 64))
                        .executes(context -> {
                            int amount = IntegerArgumentType.getInteger(context, "amount");
                            return executeMoreCommand(context.getSource(), amount);
                        })
                )
                .executes(context -> executeMoreCommand(context.getSource(), -1))
        );

        dispatcher.register(Commands.literal("emore").redirect(dispatcher.getRoot().getChild("more")));
    }

    private static int executeMoreCommand(CommandSourceStack source, int amount) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.translatable("command.more.only_player"));
            return 0;
        }

        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.isEmpty()) {
            source.sendFailure(Component.translatable("command.more.no_item"));
            return 0;
        }

        if (amount == -1) {
            amount = heldItem.getMaxStackSize();
        }

        int addAmount = Math.min(amount, heldItem.getMaxStackSize()) - heldItem.getCount();
        if (addAmount > 0) {
            heldItem.grow(addAmount);
            source.sendSuccess(Component.translatable("command.more.success", heldItem.getCount()), false);
        } else {
            source.sendFailure(Component.translatable("command.more.max_reached"));
            return 0;
        }

        return 1;
    }
}
