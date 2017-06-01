package tinker_io.registry;


import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import tinker_io.main.Main;

public class ItemRenderRegister {
	
	public static String  modid = Main.MODID;
	
	public static void registerItemRenderer() {
		//reg(ItemRegistry.SpeedUPG);
		reg(ItemRegistry.SpeedUPG, 0, "speed_upg");
		reg(ItemRegistry.SolidFuel, 0, "solid_fuel");
		reg(ItemRegistry.Lonesome_Avenue, 0, "cd_lonesome_avenue");
		reg(ItemRegistry.CrushedOre, 0, "crushed_ore");
		
		ModelBakery.registerItemVariants(ItemRegistry.Upgrade,
				getModelResourceLocation("upg_0"), 
				getModelResourceLocation("upg_1"),
				getModelResourceLocation("upg_2"),
				getModelResourceLocation("upg_3"),
				getModelResourceLocation("upg_4"),
				getModelResourceLocation("upg_5"),
				getModelResourceLocation("upg_6"),
				getModelResourceLocation("upg_7")
				);
		reg(ItemRegistry.Upgrade, 0, "upg_0");
		reg(ItemRegistry.Upgrade, 1, "upg_1");
		reg(ItemRegistry.Upgrade, 2, "upg_2");
		reg(ItemRegistry.Upgrade, 3, "upg_3");
		reg(ItemRegistry.Upgrade, 4, "upg_4");
		reg(ItemRegistry.Upgrade, 5, "upg_5");
		reg(ItemRegistry.Upgrade, 6, "upg_6");
		reg(ItemRegistry.Upgrade, 7, "upg_7");
	}
	
	public static void reg(Item item) {
		reg(item, 0, item.getUnlocalizedName().substring(5));
	}
	
	public static void reg(Item item, int meta, String filename) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
	    .register(item, meta, getModelResourceLocation(filename));
	}
	
	public static ModelResourceLocation getModelResourceLocation(String unlocalizedName) {
		return new ModelResourceLocation(modid + ":" + unlocalizedName, "inventory");
	}
	
	public static void reg2(Item item) {
		//List<ItemStack>  stacks = new ArrayList<ItemStack>();
		NonNullList<ItemStack> stacks = NonNullList.create();
		item.getSubItems(item, null, stacks);
		for (ItemStack stack : stacks) {
			item.getUnlocalizedName(stack);
		}
	}
}
