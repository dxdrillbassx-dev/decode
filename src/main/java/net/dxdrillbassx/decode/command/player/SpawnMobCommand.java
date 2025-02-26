package net.dxdrillbassx.decode.command.player;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class SpawnMobCommand {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // Регистрируем команду /spawnmob и ее алиасы
        dispatcher.register(Commands.literal("spawnmob")
                .then(Commands.argument("mob", StringArgumentType.string())
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1, 100))
                                .executes(context -> spawnMob(context.getSource(), StringArgumentType.getString(context, "mob"), IntegerArgumentType.getInteger(context, "amount"), null))
                        )
                        .executes(context -> spawnMob(context.getSource(), StringArgumentType.getString(context, "mob"), 1, null))
                )
        );

        // Алиасы для команды /spawnmob
        dispatcher.register(Commands.literal("emob")
                .then(Commands.argument("mob", StringArgumentType.string())
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1, 100))
                                .executes(context -> spawnMob(context.getSource(), StringArgumentType.getString(context, "mob"), IntegerArgumentType.getInteger(context, "amount"), null))
                        )
                        .executes(context -> spawnMob(context.getSource(), StringArgumentType.getString(context, "mob"), 1, null))
                )
        );

        dispatcher.register(Commands.literal("spawnentity")
                .then(Commands.argument("mob", StringArgumentType.string())
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1, 100))
                                .executes(context -> spawnMob(context.getSource(), StringArgumentType.getString(context, "mob"), IntegerArgumentType.getInteger(context, "amount"), null))
                        )
                        .executes(context -> spawnMob(context.getSource(), StringArgumentType.getString(context, "mob"), 1, null))
                )
        );

        dispatcher.register(Commands.literal("espawnentity")
                .then(Commands.argument("mob", StringArgumentType.string())
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1, 100))
                                .executes(context -> spawnMob(context.getSource(), StringArgumentType.getString(context, "mob"), IntegerArgumentType.getInteger(context, "amount"), null))
                        )
                        .executes(context -> spawnMob(context.getSource(), StringArgumentType.getString(context, "mob"), 1, null))
                )
        );

        dispatcher.register(Commands.literal("espawnmob")
                .then(Commands.argument("mob", StringArgumentType.string())
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1, 100))
                                .executes(context -> spawnMob(context.getSource(), StringArgumentType.getString(context, "mob"), IntegerArgumentType.getInteger(context, "amount"), null))
                        )
                        .executes(context -> spawnMob(context.getSource(), StringArgumentType.getString(context, "mob"), 1, null))
                )
        );
    }

    private static int spawnMob(CommandSourceStack source, String mobName, int amount, ServerPlayer targetPlayer) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        EntityType<? extends Mob> mobType = getMobType(mobName);
        if (mobType == null) {
            source.sendFailure(Component.literal("Невозможно найти моба с именем: " + mobName));
            return 0;
        }

        for (int i = 0; i < amount; i++) {
            Mob mob = (Mob) mobType.create(player.level);
            if (mob != null) {
                mob.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
                player.level.addFreshEntity(mob);
                source.sendSuccess(Component.literal("Моб " + mobName + " был заспаунен."), true);
            }
        }

        return 1;
    }

    private static EntityType<? extends Mob> getMobType(String mobName) {
        switch (mobName.toLowerCase()) {
            case "zombie":
                return EntityType.ZOMBIE;
            case "skeleton":
                return EntityType.SKELETON;
            case "creeper":
                return EntityType.CREEPER;
            case "cow":
                return EntityType.COW;
            case "pig":
                return EntityType.PIG;
            case "sheep":
                return EntityType.SHEEP;
            case "chicken":
                return EntityType.CHICKEN;
            case "villager":
                return EntityType.VILLAGER;
            // Добавьте сюда другие мобы по вашему усмотрению
            default:
                return null;
        }
    }
}
