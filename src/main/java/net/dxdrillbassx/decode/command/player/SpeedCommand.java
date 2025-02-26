package net.dxdrillbassx.decode.command.player;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class SpeedCommand {
    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("speed")
                .then(Commands.argument("value", FloatArgumentType.floatArg(0.1f, 10.0f))
                        .executes(context -> setSpeed(context.getSource(), FloatArgumentType.getFloat(context, "value")))));
    }

    private static int setSpeed(CommandSourceStack source, float value) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        if (player.isCreative()) {
            player.getAbilities().setFlyingSpeed(value / 10.0f); // Скорость в полете
        } else {
            player.getAbilities().setWalkingSpeed(value / 10.0f); // Скорость на земле
        }

        player.onUpdateAbilities();
        player.sendSystemMessage(Component.literal("Скорость установлена: " + value));

        return 1;
    }
}
