package net.dxdrillbassx.decode.command.player;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class RepairCommand {
    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("repair")
                .then(Commands.argument("type", StringArgumentType.string())
                        .suggests((context, builder) -> {
                            builder.suggest("hand");
                            builder.suggest("all");
                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            String type = StringArgumentType.getString(context, "type");
                            return executeRepair(context.getSource(), type);
                        }))
                .executes(context -> executeRepair(context.getSource(), "hand"))
        );

        // Альтернативные названия
        dispatcher.register(Commands.literal("fix").redirect(dispatcher.getRoot().getChild("repair")));
        dispatcher.register(Commands.literal("efix").redirect(dispatcher.getRoot().getChild("repair")));
        dispatcher.register(Commands.literal("erepair").redirect(dispatcher.getRoot().getChild("repair")));
    }

    private static int executeRepair(CommandSourceStack source, String type) {
        if (!(source.getEntity() instanceof Player player)) {
            source.sendFailure(net.minecraft.network.chat.Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        if ("hand".equals(type)) {
            ItemStack item = player.getMainHandItem();
            if (!item.isEmpty() && item.isDamageableItem()) {
                item.setDamageValue(0);
                source.sendSuccess(net.minecraft.network.chat.Component.literal("Предмет в руке отремонтирован!"), true);
            } else {
                source.sendFailure(net.minecraft.network.chat.Component.literal("В руке нет предмета, который можно починить!"));
            }
        } else if ("all".equals(type)) {
            boolean repaired = false;
            for (ItemStack stack : player.getInventory().items) {
                if (!stack.isEmpty() && stack.isDamageableItem()) {
                    stack.setDamageValue(0);
                    repaired = true;
                }
            }
            if (repaired) {
                source.sendSuccess(net.minecraft.network.chat.Component.literal("Все предметы в инвентаре отремонтированы!"), true);
            } else {
                source.sendFailure(net.minecraft.network.chat.Component.literal("В инвентаре нет предметов для починки!"));
            }
        } else {
            source.sendFailure(net.minecraft.network.chat.Component.literal("Используйте /repair [hand|all]"));
        }

        return 1;
    }
}
