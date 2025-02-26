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

        dispatcher.register(Commands.literal("exp")
                .then(Commands.literal("give")
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                .executes(context -> giveExp(context.getSource(), IntegerArgumentType.getInteger(context, "amount"))))
                )
        );
    }

    private static int giveExp(CommandSourceStack source, int amount) {

        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.translatable("command.exp.give.failure.not_player"));
            return 0;
        }

        if (amount <= 0) {
            source.sendFailure(Component.translatable("command.exp.give.failure.invalid_amount"));
            return 0;
        }

        player.giveExperiencePoints(amount);
        source.sendSuccess(Component.translatable("command.exp.give.success", amount), true);

        return 1;
    }
}
