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
            source.sendFailure(Component.translatable("command.editsign.only_players"));
            return 0;
        }

        HitResult result = player.pick(5.0, 1.0f, false);
        if (result.getType() != HitResult.Type.BLOCK) {
            source.sendFailure(Component.translatable("command.editsign.not_looking_at_block"));
            return 0;
        }

        BlockHitResult blockHitResult = (BlockHitResult) result;
        BlockPos pos = blockHitResult.getBlockPos();
        BlockState state = player.getLevel().getBlockState(pos);

        if (!(state.getBlock() instanceof SignBlock)) {
            source.sendFailure(Component.translatable("command.editsign.not_looking_at_sign"));
            return 0;
        }

        SignBlockEntity sign = (SignBlockEntity) player.getLevel().getBlockEntity(pos);
        if (sign == null) {
            source.sendFailure(Component.translatable("command.editsign.error_getting_sign"));
            return 0;
        }

        sign.setMessage(line, Component.literal(text));
        sign.setChanged();
        source.sendSuccess(Component.translatable("command.editsign.text_changed"), true);

        return 1;
    }

    private static int clearSign(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.translatable("command.editsign.only_players"));
            return 0;
        }

        HitResult result = player.pick(5.0, 1.0f, false);
        if (result.getType() != HitResult.Type.BLOCK) {
            source.sendFailure(Component.translatable("command.editsign.not_looking_at_block"));
            return 0;
        }

        BlockHitResult blockHitResult = (BlockHitResult) result;
        BlockPos pos = blockHitResult.getBlockPos();
        BlockState state = player.getLevel().getBlockState(pos);

        if (!(state.getBlock() instanceof SignBlock)) {
            source.sendFailure(Component.translatable("command.editsign.not_looking_at_sign"));
            return 0;
        }

        SignBlockEntity sign = (SignBlockEntity) player.getLevel().getBlockEntity(pos);
        if (sign == null) {
            source.sendFailure(Component.translatable("command.editsign.error_getting_sign"));
            return 0;
        }

        for (int i = 0; i < 4; i++) {
            sign.setMessage(i, Component.literal(""));
        }

        sign.setChanged();
        source.sendSuccess(Component.translatable("command.editsign.cleared"), true);

        return 1;
    }

    private static int copySign(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.translatable("command.editsign.only_players"));
            return 0;
        }

        HitResult result = player.pick(5.0, 1.0f, false);
        if (result.getType() != HitResult.Type.BLOCK) {
            source.sendFailure(Component.translatable("command.editsign.not_looking_at_block"));
            return 0;
        }

        BlockHitResult blockHitResult = (BlockHitResult) result;
        BlockPos pos = blockHitResult.getBlockPos();
        BlockState state = player.getLevel().getBlockState(pos);

        if (!(state.getBlock() instanceof SignBlock)) {
            source.sendFailure(Component.translatable("command.editsign.not_looking_at_sign"));
            return 0;
        }

        SignBlockEntity sign = (SignBlockEntity) player.getLevel().getBlockEntity(pos);
        if (sign == null) {
            source.sendFailure(Component.translatable("command.editsign.error_getting_sign"));
            return 0;
        }

        String[] copiedText = new String[4];
        for (int i = 0; i < 4; i++) {
            copiedText[i] = sign.getMessage(i, false).getString();
        }

        clipboard.put(player, copiedText);
        source.sendSuccess(Component.translatable("command.editsign.copied"), true);

        return 1;
    }

    private static int pasteSign(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.translatable("command.editsign.only_players"));
            return 0;
        }

        HitResult result = player.pick(5.0, 1.0f, false);
        if (result.getType() != HitResult.Type.BLOCK) {
            source.sendFailure(Component.translatable("command.editsign.not_looking_at_block"));
            return 0;
        }

        BlockHitResult blockHitResult = (BlockHitResult) result;
        BlockPos pos = blockHitResult.getBlockPos();
        BlockState state = player.getLevel().getBlockState(pos);

        if (!(state.getBlock() instanceof SignBlock)) {
            source.sendFailure(Component.translatable("command.editsign.not_looking_at_sign"));
            return 0;
        }

        if (!clipboard.containsKey(player)) {
            source.sendFailure(Component.translatable("command.editsign.clipboard_empty"));
            return 0;
        }

        SignBlockEntity sign = (SignBlockEntity) player.getLevel().getBlockEntity(pos);
        if (sign == null) {
            source.sendFailure(Component.translatable("command.editsign.error_getting_sign"));
            return 0;
        }

        String[] copiedText = clipboard.get(player);
        for (int i = 0; i < 4; i++) {
            sign.setMessage(i, Component.literal(copiedText[i]));
        }

        sign.setChanged();
        source.sendSuccess(Component.translatable("command.editsign.text_pasted"), true);

        return 1;
    }
}
