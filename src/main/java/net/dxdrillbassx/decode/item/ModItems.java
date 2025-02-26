package net.dxdrillbassx.decode.item;

import net.dxdrillbassx.decode.DeCode;
import net.dxdrillbassx.decode.block.ModBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DeCode.MOD_ID);

    public static final RegistryObject<Item> CUSTOM_BLOCK_ITEM = ITEMS.register("custom_block",
            () -> new BlockItem(ModBlocks.CUSTOM_BLOCK.get(), new Item.Properties()));
}
