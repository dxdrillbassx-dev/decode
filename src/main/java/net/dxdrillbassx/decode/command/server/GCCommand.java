package net.dxdrillbassx.decode.command.server;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.time.Duration;
import java.util.List;

@Mod.EventBusSubscriber
public class GCCommand {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("gc")
                .executes(context -> executeGC(context.getSource()))
        );

        dispatcher.register(Commands.literal("elag")
                .executes(context -> executeGC(context.getSource()))
        );

        dispatcher.register(Commands.literal("egc")
                .executes(context -> executeGC(context.getSource()))
        );

        dispatcher.register(Commands.literal("mem")
                .executes(context -> executeMemory(context.getSource()))
        );

        dispatcher.register(Commands.literal("emem")
                .executes(context -> executeMemory(context.getSource()))
        );

        dispatcher.register(Commands.literal("memory")
                .executes(context -> executeMemory(context.getSource()))
        );

        dispatcher.register(Commands.literal("ememory")
                .executes(context -> executeMemory(context.getSource()))
        );

        dispatcher.register(Commands.literal("uptime")
                .executes(context -> executeUptime(context.getSource()))
        );

        dispatcher.register(Commands.literal("euptime")
                .executes(context -> executeUptime(context.getSource()))
        );

        dispatcher.register(Commands.literal("entities")
                .executes(context -> executeEntities(context.getSource()))
        );

        dispatcher.register(Commands.literal("eentities")
                .executes(context -> executeEntities(context.getSource()))
        );
    }

    private static int executeGC(CommandSourceStack source) {
        // Принудительная очистка памяти
        System.gc();
        source.sendSuccess(Component.literal("Сборка мусора была инициирована."), false);
        return 1;
    }

    private static int executeMemory(CommandSourceStack source) {
        // Получаем данные о памяти с помощью MemoryMXBean
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
        long usedMemory = heapMemoryUsage.getUsed() / 1024 / 1024;  // в мегабайтах
        long maxMemory = heapMemoryUsage.getMax() / 1024 / 1024;  // в мегабайтах
        long committedMemory = heapMemoryUsage.getCommitted() / 1024 / 1024;  // в мегабайтах

        // Отправляем сообщение об использовании памяти
        source.sendSuccess(Component.literal(String.format("Использование памяти: %dМБ использовано, %dМБ зафиксировано, %dМБ максимально.", usedMemory, committedMemory, maxMemory)), false);
        return 1;
    }

    private static int executeUptime(CommandSourceStack source) {
        // Получаем время работы сервера, используя getTickCount()
        MinecraftServer server = source.getServer();
        long uptimeMillis = server.getTickCount() * 50;  // Каждый тик длится 50 миллисекунд
        Duration uptime = Duration.ofMillis(uptimeMillis);

        // Отправляем сообщение о времени работы
        source.sendSuccess(Component.literal(String.format("Время работы сервера: %d дней, %d часов, %d минут.", uptime.toDays(), uptime.toHoursPart(), uptime.toMinutesPart())), false);
        return 1;
    }

    private static int executeEntities(CommandSourceStack source) {
        // Получаем количество сущностей в мире
        MinecraftServer server = source.getServer();
        int entityCount = 0;

        // Получаем все сущности на каждом уровне (мире) сервера
        for (Level level : server.getAllLevels()) {
            // Используем AABB для определения области для поиска
            AABB boundingBox = new AABB(level.getMinBuildHeight(), level.getMinBuildHeight(), level.getMinBuildHeight(), level.getMaxBuildHeight(), level.getMaxBuildHeight(), level.getMaxBuildHeight());
            List<Entity> entities = level.getEntitiesOfClass(Entity.class, boundingBox);  // Получаем все сущности в пределах области
            entityCount += entities.size();
        }

        // Отправляем сообщение о количестве сущностей
        source.sendSuccess(Component.literal("Количество сущностей на сервере: " + entityCount), false);
        return 1;
    }
}
