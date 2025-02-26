package net.dxdrillbassx.decode.block;

import net.dxdrillbassx.decode.DeCode;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, DeCode.MOD_ID);

    public static final RegistryObject<Block> CUSTOM_BLOCK = BLOCKS.register("custom_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
}
