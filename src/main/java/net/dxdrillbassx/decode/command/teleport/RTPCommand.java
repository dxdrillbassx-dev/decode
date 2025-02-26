package net.dxdrillbassx.decode.command.teleport;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber
public class RTPCommand {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("rtp")
                .then(Commands.argument("minrange", IntegerArgumentType.integer())
                        .then(Commands.argument("maxrange", IntegerArgumentType.integer())
                                .executes(context -> {
                                    int minRange = IntegerArgumentType.getInteger(context, "minrange");
                                    int maxRange = IntegerArgumentType.getInteger(context, "maxrange");
                                    return executeRTP(context.getSource(), minRange, maxRange);
                                })
                        )
                )
        );
    }

    private static int executeRTP(CommandSourceStack source, int minRange, int maxRange) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Эту команду может использовать только игрок!"));
            return 0;
        }

        Level level = player.getLevel();
        Random random = new Random();

        // Генерация случайных координат в пределах диапазона
        int x = random.nextInt(maxRange - minRange + 1) + minRange;
        int z = random.nextInt(maxRange - minRange + 1) + minRange;

        // Попытка найти безопасную точку для телепортации
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
        if (level.getBlockState(new BlockPos(x, y, z)).is(Blocks.AIR)) {
            y = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
        }

        // Проверка на безопасность места для телепортации
        if (level.getBlockState(new BlockPos(x, y, z)).is(Blocks.WATER) || level.getBlockState(new BlockPos(x, y + 1, z)).is(Blocks.WATER)) {
            source.sendFailure(Component.literal("Невозможно телепортироваться в это место (вода)!"));
            return 0;
        }

        // Телепортация игрока
        player.teleportTo(x, y, z);

        // Отправка сообщения о телепортации
        source.sendSuccess(Component.literal("Вы были телепортированы в случайное место: " + x + ", " + y + ", " + z), false);
        return 1;
    }
}
