package net.dxdrillbassx.decode.command.chat;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.UUID;

@Mod.EventBusSubscriber
public class NickCommand {
    private static final HashMap<UUID, String> nicknames = new HashMap<>();
    private static final HashMap<UUID, String> prefixes = new HashMap<>();
    private static final HashMap<UUID, String> suffixes = new HashMap<>();

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("nick")
                .then(Commands.argument("nickname", StringArgumentType.string())
                        .executes(context -> setNick(context.getSource(), StringArgumentType.getString(context, "nickname"))))
                .then(Commands.literal("prefix")
                        .then(Commands.argument("prefix", StringArgumentType.string())
                                .executes(context -> setPrefix(context.getSource(), StringArgumentType.getString(context, "prefix")))))
                .then(Commands.literal("suffix")
                        .then(Commands.argument("suffix", StringArgumentType.string())
                                .executes(context -> setSuffix(context.getSource(), StringArgumentType.getString(context, "suffix")))))
                .executes(context -> resetNick(context.getSource()))
        );
    }

    private static int setNick(CommandSourceStack source, String nickname) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        if (nickname.equalsIgnoreCase("reset")) {
            return resetNick(source);
        }

        nicknames.put(player.getUUID(), nickname);
        player.sendSystemMessage(Component.literal("Ваш ник теперь: " + getFullNick(player)));
        return 1;
    }

    private static int setPrefix(CommandSourceStack source, String prefix) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        prefixes.put(player.getUUID(), prefix);
        player.sendSystemMessage(Component.literal("Ваш префикс установлен: " + prefix));
        return 1;
    }

    private static int setSuffix(CommandSourceStack source, String suffix) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        suffixes.put(player.getUUID(), suffix);
        player.sendSystemMessage(Component.literal("Ваш суффикс установлен: " + suffix));
        return 1;
    }

    private static int resetNick(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        nicknames.remove(player.getUUID());
        prefixes.remove(player.getUUID());
        suffixes.remove(player.getUUID());

        player.sendSystemMessage(Component.literal("Ваш ник был сброшен."));
        return 1;
    }

    private static String getFullNick(ServerPlayer player) {
        String prefix = prefixes.getOrDefault(player.getUUID(), "");
        String nickname = nicknames.getOrDefault(player.getUUID(), player.getName().getString());
        String suffix = suffixes.getOrDefault(player.getUUID(), "");
        return (prefix + " " + nickname + " " + suffix).trim();
    }
}
