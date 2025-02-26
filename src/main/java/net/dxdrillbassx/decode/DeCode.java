package net.dxdrillbassx.decode;

import net.dxdrillbassx.decode.block.ModBlocks;
import net.dxdrillbassx.decode.item.ModItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(DeCode.MOD_ID)
public class DeCode {
    public static final String MOD_ID = "decode";

    public DeCode() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }
}
