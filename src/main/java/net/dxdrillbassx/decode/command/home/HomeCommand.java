package net.dxdrillbassx.decode.command.home;

import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber
public class HomeCommand {
    private static final int MAX_HOMES = 3;
    private static final Map<UUID, Map<String, Vec3>> homePoints = Maps.newHashMap();

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("sethome")
                .then(Commands.argument("name", StringArgumentType.word())
                        .executes(context -> setHome(context.getSource(),
                                StringArgumentType.getString(context, "name")))));

        dispatcher.register(Commands.literal("home")
                .then(Commands.argument("name", StringArgumentType.word())
                        .executes(context -> teleportHome(context.getSource(),
                                StringArgumentType.getString(context, "name")))));

        dispatcher.register(Commands.literal("homes")
                .executes(context -> listHomes(context.getSource())));

        HomeData.loadHomes(homePoints);
    }

    private static int setHome(CommandSourceStack source, String name) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        UUID playerId = player.getUUID();
        Map<String, Vec3> homes = homePoints.computeIfAbsent(playerId, k -> Maps.newHashMap());

        if (homes.size() >= MAX_HOMES) {
            source.sendFailure(Component.literal("Вы можете иметь только " + MAX_HOMES + " точки дома!"));
            return 0;
        }

        homes.put(name, player.position());
        HomeData.saveHomes(homePoints);
        source.sendSuccess(Component.literal("Точка дома '" + name + "' сохранена!").withStyle(s -> s.withColor(0x00FF00)), true);
        return 1;
    }

    private static int teleportHome(CommandSourceStack source, String name) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        UUID playerId = player.getUUID();
        Map<String, Vec3> homes = homePoints.get(playerId);

        if (homes == null || !homes.containsKey(name)) {
            source.sendFailure(Component.literal("Точка дома '" + name + "' не найдена!"));
            return 0;
        }

        player.teleportTo(homes.get(name).x, homes.get(name).y, homes.get(name).z);
        source.sendSuccess(Component.literal("Телепортация к дому '" + name + "'").withStyle(s -> s.withColor(0x00AAFF)), true);
        return 1;
    }

    private static int listHomes(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        UUID playerId = player.getUUID();
        Map<String, Vec3> homes = homePoints.get(playerId);

        if (homes == null || homes.isEmpty()) {
            source.sendFailure(Component.literal("У вас нет сохраненных точек дома!"));
            return 0;
        }

        StringBuilder homeList = new StringBuilder("Ваши точки дома: ");
        homes.forEach((name, pos) -> homeList.append("\n - ").append(name)
                .append(" (X: ").append((int) pos.x)
                .append(", Y: ").append((int) pos.y)
                .append(", Z: ").append((int) pos.z).append(")"));

        source.sendSuccess(Component.literal(homeList.toString()).withStyle(s -> s.withColor(0xFFFF00)), false);
        return 1;
    }
}
