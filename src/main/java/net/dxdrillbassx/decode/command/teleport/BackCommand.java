package net.dxdrillbassx.decode.command.teleport;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.UUID;

@Mod.EventBusSubscriber
public class BackCommand {

    private static final HashMap<UUID, double[]> lastLocations = new HashMap<>();

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // Регистрация команды /back
        dispatcher.register(Commands.literal("back")
                .executes(context -> returnToLastLocation(context.getSource())));
    }

    // Сохраняем позицию игрока перед телепортацией
    @SubscribeEvent
    public static void onPlayerTeleport(PlayerEvent.PlayerChangedDimensionEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        saveLastLocation(player);
    }

    // Сохраняем позицию игрока перед смертью
    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            saveLastLocation(player);
        }
    }

    private static void saveLastLocation(ServerPlayer player) {
        lastLocations.put(player.getUUID(), new double[]{player.getX(), player.getY(), player.getZ()});
    }

    private static int returnToLastLocation(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        UUID playerUUID = player.getUUID();
        if (!lastLocations.containsKey(playerUUID)) {
            source.sendFailure(Component.literal("Нет сохраненной позиции для возврата!"));
            return 0;
        }

        double[] lastPos = lastLocations.get(playerUUID);
        player.teleportTo(lastPos[0], lastPos[1], lastPos[2]);
        player.sendSystemMessage(Component.literal("Вы вернулись на предыдущую позицию!"));
        return 1;
    }
}
