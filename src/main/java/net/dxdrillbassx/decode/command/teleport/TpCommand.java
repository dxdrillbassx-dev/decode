package net.dxdrillbassx.decode.command.teleport;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class TpCommand {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // Команда /tp [игрок] [целевой игрок]
        dispatcher.register(Commands.literal("tp")
                .then(Commands.argument("target", EntityArgument.player())
                        .executes(context -> tpPlayerToPlayer(context.getSource(), EntityArgument.getPlayer(context, "target"))))

                // Команда /tp [игрок] [x] [y] [z]
                .then(Commands.argument("x", IntegerArgumentType.integer())
                        .then(Commands.argument("y", IntegerArgumentType.integer())
                                .then(Commands.argument("z", IntegerArgumentType.integer())
                                        .executes(context -> tpPlayerToCoords(context.getSource(),
                                                IntegerArgumentType.getInteger(context, "x"),
                                                IntegerArgumentType.getInteger(context, "y"),
                                                IntegerArgumentType.getInteger(context, "z")))))));
    }

    // Телепортация игрока к другому игроку
    private static int tpPlayerToPlayer(CommandSourceStack source, Player targetPlayer) {
        if (!(source.getEntity() instanceof Player player)) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        player.teleportTo(targetPlayer.getX(), targetPlayer.getY(), targetPlayer.getZ());
        source.sendSuccess(Component.literal("Телепортирован к игроку: " + targetPlayer.getName().getString()), false);
        return 1;
    }

    // Телепортация игрока на заданные координаты
    private static int tpPlayerToCoords(CommandSourceStack source, int x, int y, int z) {
        if (!(source.getEntity() instanceof Player player)) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        player.teleportTo(x, y, z);
        source.sendSuccess(Component.literal("Телепортирован на координаты: " + x + ", " + y + ", " + z), false);
        return 1;
    }
}
