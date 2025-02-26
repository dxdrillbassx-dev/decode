package net.dxdrillbassx.decode.command.player;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class SpawnpointCommand {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("spawnpoint")
                .then(Commands.argument("player", StringArgumentType.string())
                        .then(Commands.argument("x", IntegerArgumentType.integer())
                                .then(Commands.argument("y", IntegerArgumentType.integer())
                                        .then(Commands.argument("z", IntegerArgumentType.integer())
                                                .executes(context -> {
                                                    String playerName = StringArgumentType.getString(context, "player");
                                                    int x = IntegerArgumentType.getInteger(context, "x");
                                                    int y = IntegerArgumentType.getInteger(context, "y");
                                                    int z = IntegerArgumentType.getInteger(context, "z");
                                                    return executeSpawnpoint(context.getSource(), playerName, x, y, z);
                                                })
                                        )
                                )
                        )
                )
                .executes(context -> executeSpawnpoint(context.getSource(), null, null, null, null))
        );
    }

    private static int executeSpawnpoint(CommandSourceStack source, String playerName, Integer x, Integer y, Integer z) {
        ServerPlayer player;

        if (playerName != null) {
            // Получаем игрока по имени
            player = source.getServer().getPlayerList().getPlayerByName(playerName);
            if (player == null) {
                source.sendFailure(Component.literal("Игрок с таким именем не найден!"));
                return 0;
            }
        } else {
            if (!(source.getEntity() instanceof ServerPlayer)) {
                source.sendFailure(Component.literal("Эту команду может использовать только игрок!"));
                return 0;
            }
            player = (ServerPlayer) source.getEntity();
        }

        if (x == null || y == null || z == null) {
            // Устанавливаем точку спауна для игрока на его текущие координаты
            BlockPos pos = player.blockPosition();  // Получаем текущую позицию игрока
            player.setRespawnPosition(player.level.dimension(), pos, 0.0F, true, false);  // Передаем параметры
            source.sendSuccess(Component.literal("Точка возрождения игрока " + player.getName().getString() + " установлена на его текущие координаты."), true);
        } else {
            // Устанавливаем точку спауна на указанные координаты
            BlockPos pos = new BlockPos(x, y, z);
            player.setRespawnPosition(player.level.dimension(), pos, 0.0F, true, false);  // Передаем параметры
            source.sendSuccess(Component.literal("Точка возрождения игрока " + player.getName().getString() + " установлена на координатах (" + x + ", " + y + ", " + z + ")."), true);
        }

        return 1;
    }
}
