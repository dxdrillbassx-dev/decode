package net.dxdrillbassx.decode.command.player;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class FlyCommand {
    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // Регистрируем команду /fly
        dispatcher.register(Commands.literal("fly")
                .executes(context -> toggleFly(context.getSource()))
        );

        // Альтернативные названия
        dispatcher.register(Commands.literal("efly").redirect(dispatcher.getRoot().getChild("fly")));
    }

    private static int toggleFly(CommandSourceStack source) {
        // Проверяем, является ли командующий игроком
        if (!(source.getEntity() instanceof Player player)) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        // Проверяем, включен ли режим полета
        boolean isFlying = player.getAbilities().flying;

        // Переключаем режим полета
        if (isFlying) {
            player.getAbilities().flying = false;
            player.getAbilities().mayfly = false;  // Разрешение на полет в общем случае
            player.sendSystemMessage(Component.literal("Режим полета отключен"));
        } else {
            player.getAbilities().flying = true;
            player.getAbilities().mayfly = true;  // Разрешение на полет
            player.sendSystemMessage(Component.literal("Режим полета включен"));
        }

        // Обновляем способности игрока
        player.onUpdateAbilities();

        return 1;
    }
}
