package net.dxdrillbassx.decode.command.player;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class BookCommand {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // Регистрируем команду /book
        dispatcher.register(Commands.literal("book")
                .then(Commands.argument("bookIdentifier", StringArgumentType.string())
                        .executes(context -> openBook(context.getSource(), StringArgumentType.getString(context, "bookIdentifier")))
                )
        );

        // Алиас для команды /ebook
        dispatcher.register(Commands.literal("ebook")
                .then(Commands.argument("bookIdentifier", StringArgumentType.string())
                        .executes(context -> openBook(context.getSource(), StringArgumentType.getString(context, "bookIdentifier")))
                )
        );
    }

    private static int openBook(CommandSourceStack source, String bookIdentifier) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Только игроки могут использовать эту команду!"));
            return 0;
        }

        // Создаем или находим книгу (например, написанную книгу)
        ItemStack bookStack = new ItemStack(Items.WRITTEN_BOOK);  // создаем книгу

        // Выдаем книгу игроку
        player.addItem(bookStack);

        // Можно использовать текст, чтобы дать информацию игроку
        source.sendSuccess(Component.literal("Книга с идентификатором '" + bookIdentifier + "' выдана игроку."), true);

        return 1;
    }
}
