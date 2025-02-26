package net.dxdrillbassx.decode.command.admin;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class KillAllCommand {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // Регистрируем команду /killall
        dispatcher.register(Commands.literal("killall")
                .executes(context -> killAllEntities(context.getSource()))
        );
    }

    private static int killAllEntities(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Эту команду может использовать только игрок!"));
            return 0;
        }

        Level level = player.getLevel();

        // Используем getEntitiesOfClass для получения всех живых существ
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(100))) {  // Увеличиваем радиус на 100 блоков
            entity.kill();
        }

        source.sendSuccess(Component.literal("Все сущности в радиусе были убиты!"), false);
        return 1;
    }
}
