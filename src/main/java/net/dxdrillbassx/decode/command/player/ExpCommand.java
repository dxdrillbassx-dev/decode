package net.dxdrillbassx.decode.command.player;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ExpCommand {
    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // Регистрируем команду /exp give
        dispatcher.register(Commands.literal("exp")
                .then(Commands.literal("give")
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                .executes(context -> giveExp(context.getSource(), IntegerArgumentType.getInteger(context, "amount"))))
                )
        );
    }

    private static int giveExp(CommandSourceStack source, int amount) {
        // Проверяем, является ли командующий игроком
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        // Выдаем опыт игроку
        player.giveExperiencePoints(amount);
        player.sendSystemMessage(Component.literal("Вы получили " + amount + " опыта!"));

        return 1;
    }
}
