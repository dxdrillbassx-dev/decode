package net.dxdrillbassx.decode.command.edit;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber
public class EditSignCommand {

    // Буфер обмена для хранения текста табличек
    private static final Map<ServerPlayer, String[]> clipboard = new HashMap<>();

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("editsign")
                .then(Commands.literal("set")
                        .then(Commands.argument("line", IntegerArgumentType.integer(0, 3))
                                .then(Commands.argument("text", StringArgumentType.string())
                                        .executes(context -> {
                                            int line = IntegerArgumentType.getInteger(context, "line");
                                            String text = StringArgumentType.getString(context, "text");
                                            return setSignText(context.getSource(), line, text);
                                        }))))
                .then(Commands.literal("clear")
                        .executes(context -> clearSign(context.getSource())))
                .then(Commands.literal("copy")
                        .executes(context -> copySign(context.getSource())))
                .then(Commands.literal("paste")
                        .executes(context -> pasteSign(context.getSource())))
        );
    }

    private static int setSignText(CommandSourceStack source, int line, String text) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        // Получаем луч, на который смотрит игрок
        HitResult result = player.pick(5.0, 1.0f, false); // 5.0 - дистанция, 1.0 - угол обзора
        if (result.getType() != HitResult.Type.BLOCK) {
            source.sendFailure(Component.literal("Не на блок смотрите!"));
            return 0;
        }

        BlockHitResult blockHitResult = (BlockHitResult) result;
        BlockPos pos = blockHitResult.getBlockPos();
        BlockState state = player.getLevel().getBlockState(pos);

        if (!(state.getBlock() instanceof SignBlock)) {
            source.sendFailure(Component.literal("Вы не смотрите на табличку!"));
            return 0;
        }

        SignBlockEntity sign = (SignBlockEntity) player.getLevel().getBlockEntity(pos);
        if (sign == null) {
            source.sendFailure(Component.literal("Ошибка получения таблички!"));
            return 0;
        }

        sign.setMessage(line, Component.literal(text));
        sign.setChanged();
        source.sendSuccess(Component.literal("Текст на табличке изменён!"), true);

        return 1;
    }

    private static int clearSign(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        // Получаем луч, на который смотрит игрок
        HitResult result = player.pick(5.0, 1.0f, false);
        if (result.getType() != HitResult.Type.BLOCK) {
            source.sendFailure(Component.literal("Не на блок смотрите!"));
            return 0;
        }

        BlockHitResult blockHitResult = (BlockHitResult) result;
        BlockPos pos = blockHitResult.getBlockPos();
        BlockState state = player.getLevel().getBlockState(pos);

        if (!(state.getBlock() instanceof SignBlock)) {
            source.sendFailure(Component.literal("Вы не смотрите на табличку!"));
            return 0;
        }

        SignBlockEntity sign = (SignBlockEntity) player.getLevel().getBlockEntity(pos);
        if (sign == null) {
            source.sendFailure(Component.literal("Ошибка получения таблички!"));
            return 0;
        }

        for (int i = 0; i < 4; i++) {
            sign.setMessage(i, Component.literal(""));
        }

        sign.setChanged();
        source.sendSuccess(Component.literal("Табличка очищена!"), true);

        return 1;
    }

    private static int copySign(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        // Получаем луч, на который смотрит игрок
        HitResult result = player.pick(5.0, 1.0f, false);
        if (result.getType() != HitResult.Type.BLOCK) {
            source.sendFailure(Component.literal("Не на блок смотрите!"));
            return 0;
        }

        BlockHitResult blockHitResult = (BlockHitResult) result;
        BlockPos pos = blockHitResult.getBlockPos();
        BlockState state = player.getLevel().getBlockState(pos);

        if (!(state.getBlock() instanceof SignBlock)) {
            source.sendFailure(Component.literal("Вы не смотрите на табличку!"));
            return 0;
        }

        SignBlockEntity sign = (SignBlockEntity) player.getLevel().getBlockEntity(pos);
        if (sign == null) {
            source.sendFailure(Component.literal("Ошибка получения таблички!"));
            return 0;
        }

        String[] copiedText = new String[4];
        for (int i = 0; i < 4; i++) {
            copiedText[i] = sign.getMessage(i, false).getString();
        }

        clipboard.put(player, copiedText);
        source.sendSuccess(Component.literal("Табличка скопирована!"), true);

        return 1;
    }

    private static int pasteSign(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        // Получаем луч, на который смотрит игрок
        HitResult result = player.pick(5.0, 1.0f, false);
        if (result.getType() != HitResult.Type.BLOCK) {
            source.sendFailure(Component.literal("Не на блок смотрите!"));
            return 0;
        }

        BlockHitResult blockHitResult = (BlockHitResult) result;
        BlockPos pos = blockHitResult.getBlockPos();
        BlockState state = player.getLevel().getBlockState(pos);

        if (!(state.getBlock() instanceof SignBlock)) {
            source.sendFailure(Component.literal("Вы не смотрите на табличку!"));
            return 0;
        }

        if (!clipboard.containsKey(player)) {
            source.sendFailure(Component.literal("Буфер обмена пуст!"));
            return 0;
        }

        SignBlockEntity sign = (SignBlockEntity) player.getLevel().getBlockEntity(pos);
        if (sign == null) {
            source.sendFailure(Component.literal("Ошибка получения таблички!"));
            return 0;
        }

        String[] copiedText = clipboard.get(player);
        for (int i = 0; i < 4; i++) {
            sign.setMessage(i, Component.literal(copiedText[i]));
        }

        sign.setChanged();
        source.sendSuccess(Component.literal("Текст вставлен в табличку!"), true);

        return 1;
    }
}
