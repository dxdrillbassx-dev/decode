package net.dxdrillbassx.decode.command.teleport;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class TopCommand {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // Регистрация команды top
        dispatcher.register(Commands.literal("top")
                .executes(context -> {
                    return executeTopCommand(context.getSource());
                })
        );

        // Альтернативные имена команды
        dispatcher.register(Commands.literal("etop").redirect(dispatcher.getRoot().getChild("top")));
    }

    private static int executeTopCommand(CommandSourceStack source) throws CommandSyntaxException {
        Player player = source.getPlayerOrException();
        Level world = player.level;

        // Получение текущих координат игрока
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();

        // Преобразование координат x, z в целочисленные значения
        int blockX = (int) x;
        int blockZ = (int) z;

        // Получение самой высокой точки по координатам (x, z)
        // Используем Heightmap для получения высоты
        BlockPos topPos = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos(blockX, 0, blockZ));

        // Телепортация игрока на самую высокую точку
        Vec3 topPosition = new Vec3(topPos.getX(), topPos.getY(), topPos.getZ());
        player.teleportTo(topPosition.x, topPosition.y, topPosition.z);

        // Отправка сообщения игроку
        source.sendSuccess(Component.literal("Вы телепортированы на самую высокую точку."), false);
        return 1;
    }
}
