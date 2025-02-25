package net.dxdrillbassx.decode;

import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.dxdrillbassx.decode.commands.AfkCommand; // Добавляем импорт для команды AFK
import org.slf4j.Logger;

@Mod(DeCode.MOD_ID)
public class DeCode {
    public static final String MOD_ID = "decode";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static final RegistryObject<Block> CUSTOM_BLOCK = BLOCKS.register("custom_block",
            () -> new Block(BlockBehaviour.Properties.of(Material.STONE).strength(3.0f)));

    public static final RegistryObject<Item> CUSTOM_BLOCK_ITEM = ITEMS.register("custom_block",
            () -> new BlockItem(CUSTOM_BLOCK.get(), new Item.Properties()));

    public DeCode() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);

        // Добавляем слушатель для события CreativeModeTabEvent
        modEventBus.addListener(DeCode::registerCreativeTabs);

        // Регистрируем команду AFK вручную
        modEventBus.addListener(this::onServerStarting);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public static void registerCreativeTabs(CreativeModeTabEvent.Register event) {
        event.registerCreativeModeTab(new net.minecraft.resources.ResourceLocation(MOD_ID, "decode_tab"),
                builder -> builder
                        .title(Component.translatable("itemGroup.decode_tab"))
                        .icon(() -> new ItemStack(CUSTOM_BLOCK_ITEM.get()))
                        .displayItems((parameters, output) -> output.accept(CUSTOM_BLOCK_ITEM.get()))
        );
    }

    // Этот метод будет вызван при старте сервера
    public void onServerStarting(ServerStartingEvent event) {
        AfkCommand.registerCommands(event);  // Регистрация команды AFK
    }
}
