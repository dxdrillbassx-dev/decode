package net.dxdrillbassx.decode.command.teleport;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber
public class TpAutoCommand {

    // Хранение состояния "автоподтверждения" для каждого игрока
    private static final Map<String, Boolean> tpAutoStatus = new HashMap<>();

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // Регистрация команды tpauto
        dispatcher.register(Commands.literal("tpauto")
                .then(Commands.argument("player", StringArgumentType.string())
                        .executes(context -> {
                            String playerName = StringArgumentType.getString(context, "player");
                            return executeTpAuto(context.getSource(), playerName);
                        })
                )
                .executes(context -> {
                    // Если игрок не указан, то применяем команду к себе
                    return executeTpAuto(context.getSource(), null);
                })
        );

        // Альтернативные названия команды
        dispatcher.register(Commands.literal("etpauto").redirect(dispatcher.getRoot().getChild("tpauto")));
    }

    private static int executeTpAuto(CommandSourceStack source, String playerName) {
        MinecraftServer server = source.getServer();
        ServerPlayer executor = (ServerPlayer) source.getEntity();

        if (executor == null) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        if (playerName != null) {
            // Получаем указанного игрока
            ServerPlayer targetPlayer = server.getPlayerList().getPlayerByName(playerName);
            if (targetPlayer == null) {
                source.sendFailure(Component.literal("Игрок с таким именем не найден!"));
                return 0;
            }

            // Переключаем статус автоподтверждения для указанного игрока
            boolean currentStatus = tpAutoStatus.getOrDefault(playerName, false);
            tpAutoStatus.put(playerName, !currentStatus);

            if (tpAutoStatus.get(playerName)) {
                executor.sendSystemMessage(Component.literal(playerName + " теперь автоматически принимает запросы на телепортацию."));
            } else {
                executor.sendSystemMessage(Component.literal(playerName + " теперь не принимает запросы на телепортацию."));
            }
        } else {
            // Переключаем статус автоподтверждения для себя
            boolean currentStatus = tpAutoStatus.getOrDefault(executor.getName().getString(), false);
            tpAutoStatus.put(executor.getName().getString(), !currentStatus);

            if (tpAutoStatus.get(executor.getName().getString())) {
                executor.sendSystemMessage(Component.literal("Теперь вы автоматически принимаете запросы на телепортацию."));
            } else {
                executor.sendSystemMessage(Component.literal("Теперь вы не принимаете запросы на телепортацию."));
            }
        }

        return 1;
    }

    // Получить статус автоподтверждения телепортации для игрока
    public static boolean isTpAutoEnabled(String playerName) {
        return tpAutoStatus.getOrDefault(playerName, false);
    }
}
