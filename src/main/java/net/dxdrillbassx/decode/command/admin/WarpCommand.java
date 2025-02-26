package net.dxdrillbassx.decode.command.admin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Mod.EventBusSubscriber
public class WarpCommand {

    private static final File WARPS_FILE = new File("config/warps.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type TYPE = new TypeToken<Map<String, double[]>>() {}.getType();
    private static Map<String, double[]> warps = new HashMap<>();

    static {
        loadWarps();
    }

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("setwarp")
                .then(Commands.argument("name", StringArgumentType.word())
                        .executes(context -> setWarp(context.getSource(), StringArgumentType.getString(context, "name")))));

        dispatcher.register(Commands.literal("warp")
                .then(Commands.argument("name", StringArgumentType.word())
                        .executes(context -> teleportToWarp(context.getSource(), StringArgumentType.getString(context, "name"))))
                .then(Commands.literal("list")
                        .executes(context -> listWarps(context.getSource()))));

        dispatcher.register(Commands.literal("delwarp")
                .then(Commands.argument("name", StringArgumentType.word())
                        .executes(context -> deleteWarp(context.getSource(), StringArgumentType.getString(context, "name")))));
    }

    private static int setWarp(CommandSourceStack source, String name) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        warps.put(name, new double[]{player.getX(), player.getY(), player.getZ()});
        saveWarps();

        player.sendSystemMessage(Component.literal("Варп '" + name + "' установлен!"));
        return 1;
    }

    private static int teleportToWarp(CommandSourceStack source, String name) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        if (!warps.containsKey(name)) {
            source.sendFailure(Component.literal("Варп '" + name + "' не найден!"));
            return 0;
        }

        double[] pos = warps.get(name);
        player.teleportTo(pos[0], pos[1], pos[2]);
        player.sendSystemMessage(Component.literal("Телепортация к варпу '" + name + "'!"));
        return 1;
    }

    private static int deleteWarp(CommandSourceStack source, String name) {
        if (!warps.containsKey(name)) {
            source.sendFailure(Component.literal("Варп '" + name + "' не найден!"));
            return 0;
        }

        warps.remove(name);
        saveWarps();

        source.sendSuccess(Component.literal("Варп '" + name + "' удален!"), true);
        return 1;
    }

    private static int listWarps(CommandSourceStack source) {
        if (warps.isEmpty()) {
            source.sendFailure(Component.literal("Нет доступных варпов!"));
            return 0;
        }

        Set<String> warpNames = warps.keySet();
        source.sendSuccess(Component.literal("Доступные варпы: " + String.join(", ", warpNames)), false);
        return 1;
    }

    private static void loadWarps() {
        if (!WARPS_FILE.exists()) return;

        try (FileReader reader = new FileReader(WARPS_FILE)) {
            warps = GSON.fromJson(reader, TYPE);
        } catch (Exception e) {
            System.err.println("Ошибка при загрузке варпов: " + e.getMessage());
        }
    }

    private static void saveWarps() {
        try (FileWriter writer = new FileWriter(WARPS_FILE)) {
            GSON.toJson(warps, writer);
        } catch (Exception e) {
            System.err.println("Ошибка при сохранении варпов: " + e.getMessage());
        }
    }
}
