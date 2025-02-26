package net.dxdrillbassx.decode.command.admin;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class SpawnCommand {
    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("spawn")
                .executes(context -> teleportToSpawn(context.getSource()))
        );
    }

    private static int teleportToSpawn(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.translatable("command.spawn.only_player"));
            return 0;
        }

        double spawnX = player.getLevel().getLevelData().getXSpawn();
        double spawnY = player.getLevel().getLevelData().getYSpawn();
        double spawnZ = player.getLevel().getLevelData().getZSpawn();

        player.teleportTo(spawnX, spawnY, spawnZ);
        player.sendSystemMessage(Component.translatable("command.spawn.success"));

        return 1;
    }
}
