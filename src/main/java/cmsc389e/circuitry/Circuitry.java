package cmsc389e.circuitry;

import java.util.ArrayList;

import cmsc389e.circuitry.common.Config;
import cmsc389e.circuitry.common.NodeTileEntity;
import cmsc389e.circuitry.common.block.InNodeBlock;
import cmsc389e.circuitry.common.block.OutNodeBlock;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.tileentity.TileEntityType.Builder;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(Circuitry.MODID)
@EventBusSubscriber(bus = Bus.MOD)
public class Circuitry {
    public static final String MODID = "circuitry";

    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<TileEntityType<?>> TE = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES,
	    MODID);

    public static final RegistryObject<Block> IN_NODE_BLOCK = BLOCKS.register("in_node", InNodeBlock::new),
	    OUT_NODE_BLOCK = BLOCKS.register("out_node", OutNodeBlock::new);
    public static final RegistryObject<TileEntityType<?>> TYPE = TE.register("node",
	    () -> Builder.create(NodeTileEntity::new, IN_NODE_BLOCK.get(), OUT_NODE_BLOCK.get()).build(null));

    static {
	ITEMS.register("in_node", () -> new BlockItem(IN_NODE_BLOCK.get(), new Properties().group(ItemGroup.REDSTONE)));
	ITEMS.register("out_node",
		() -> new BlockItem(OUT_NODE_BLOCK.get(), new Properties().group(ItemGroup.REDSTONE)));
    }

    @SuppressWarnings("resource")
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
	ObfuscationReflectionHelper.setPrivateValue(NewChatGui.class,
		event.getMinecraftSupplier().get().ingameGUI.getChatGUI(), new ArrayList<ChatLine>() {
		    private boolean frozen;

		    @Override
		    public ChatLine remove(int index) {
			frozen = true;
			return get(index);
		    }

		    @Override
		    public int size() {
			int size = frozen ? 0 : super.size();
			frozen = false;
			return size;
		    }
		}, "field_146253_i"); // drawnChatLines
    }

    public Circuitry() {
	IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
	BLOCKS.register(bus);
	ITEMS.register(bus);
	TE.register(bus);

	Config.register();
    }
}