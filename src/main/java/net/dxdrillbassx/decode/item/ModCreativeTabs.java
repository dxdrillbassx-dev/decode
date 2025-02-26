package net.dxdrillbassx.decode.item;

import net.dxdrillbassx.decode.DeCode;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCreativeTabs {
    @SubscribeEvent
    public static void registerCreativeTabs(CreativeModeTabEvent.Register event) {
        event.registerCreativeModeTab(new ResourceLocation(DeCode.MOD_ID, "decode_tab"),
                builder -> builder
                        .title(Component.translatable("itemGroup.decode_tab"))
                        .icon(() -> new ItemStack(ModItems.CUSTOM_BLOCK_ITEM.get()))
                        .displayItems((parameters, output) -> output.accept(ModItems.CUSTOM_BLOCK_ITEM.get()))
        );
    }
}
