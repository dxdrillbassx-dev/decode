package net.dxdrillbassx.decode.command.teleport;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.mojang.brigadier.arguments.StringArgumentType;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber
public class TpaCommand {

    // Сохраняем запросы на телепортацию
    private static final Map<Player, Player> tpaRequests = new HashMap<>();

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // Регистрируем команду /tpa
        dispatcher.register(Commands.literal("tpa")
                .then(Commands.argument("player", StringArgumentType.word())
                        .executes(context -> sendTpaRequest(context.getSource(), StringArgumentType.getString(context, "player")))
                )
        );

        // Регистрируем команду для принятия запроса
        dispatcher.register(Commands.literal("tpaccept")
                .executes(context -> acceptTpaRequest(context.getSource()))
        );

        // Регистрируем команду для отклонения запроса
        dispatcher.register(Commands.literal("tpdeny")
                .executes(context -> denyTpaRequest(context.getSource()))
        );
    }

    private static int sendTpaRequest(CommandSourceStack source, String targetName) {
        // Проверяем, является ли командующий игроком
        if (!(source.getEntity() instanceof Player player)) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        // Находим игрока по имени
        Player target = source.getServer().getPlayerList().getPlayerByName(targetName);
        if (target == null) {
            source.sendFailure(Component.literal("Игрок с таким именем не найден"));
            return 0;
        }

        // Отправляем запрос на телепортацию
        tpaRequests.put(target, player);
        target.sendSystemMessage(Component.literal(player.getName().getString() + " отправил вам запрос на телепортацию. Введите /tpaccept для принятия, /tpdeny для отклонения"));

        source.sendSuccess(Component.literal("Запрос на телепортацию отправлен игроку " + targetName), false);
        return 1;
    }

    private static int acceptTpaRequest(CommandSourceStack source) {
        if (!(source.getEntity() instanceof Player player)) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        // Проверяем, есть ли запрос от другого игрока
        Player sender = tpaRequests.get(player);
        if (sender == null) {
            source.sendFailure(Component.literal("У вас нет запроса на телепортацию"));
            return 0;
        }

        // Телепортируем отправителя к игроку
        sender.teleportTo(player.getX(), player.getY(), player.getZ());
        sender.sendSystemMessage(Component.literal("Вы были телепортированы к " + player.getName().getString()));

        // Убираем запрос
        tpaRequests.remove(player);
        return 1;
    }

    private static int denyTpaRequest(CommandSourceStack source) {
        if (!(source.getEntity() instanceof Player player)) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        // Проверяем, есть ли запрос от другого игрока
        Player sender = tpaRequests.get(player);
        if (sender == null) {
            source.sendFailure(Component.literal("У вас нет запроса на телепортацию"));
            return 0;
        }

        // Отклоняем запрос
        sender.sendSystemMessage(Component.literal(player.getName().getString() + " отклонил ваш запрос на телепортацию"));
        tpaRequests.remove(player);

        return 1;
    }
}
