package net.dxdrillbassx.decode.command.admin;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class SpawnCommand {
    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // Регистрируем команду /spawn
        dispatcher.register(Commands.literal("spawn")
                .executes(context -> teleportToSpawn(context.getSource()))
        );
    }

    private static int teleportToSpawn(CommandSourceStack source) {
        // Проверяем, является ли командующий игроком
        if (!(source.getEntity() instanceof Player player)) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        // Получаем координаты спауна
        Player entity = player;
        double spawnX = entity.level.getLevelData().getXSpawn();
        double spawnY = entity.level.getLevelData().getYSpawn();
        double spawnZ = entity.level.getLevelData().getZSpawn();

        // Телепортируем игрока на точку спауна
        entity.teleportTo(spawnX, spawnY, spawnZ);

        // Уведомляем игрока
        player.sendSystemMessage(Component.literal("Вы были телепортированы на спаун!"));

        return 1;
    }
}
