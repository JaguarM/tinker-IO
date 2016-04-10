package tinker_io.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.tconstruct.smeltery.tileentity.TileTank;
import tinker_io.TileEntity.StirlingEngineTileEntity;
import tinker_io.main.Main;

public class StirlingEngine extends BlockContainer {
	
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	
	public StirlingEngine(String unlocalizedName){
		super(Material.rock);
		setUnlocalizedName(unlocalizedName);
		setCreativeTab(Main.TinkerIOTabs);
		setHarvestLevel("pickaxe", 1);
		setHardness(3);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}
	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[] { FACING });
	}

	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new StirlingEngineTileEntity();
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
        EnumFacing enumfacing = EnumFacing.getFront(meta);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y)
        {
            enumfacing = EnumFacing.NORTH;
        }
		return getDefaultState().withProperty(FACING, enumfacing);
	}
	
	@Override
	public int getMetaFromState(IBlockState state)
	{
        return ((EnumFacing)state.getValue(FACING)).getIndex();
	}
	
	@Override
    public boolean onBlockEventReceived(World worldIn, BlockPos pos, IBlockState state, int eventID, int eventParam) {
        super.onBlockEventReceived(worldIn, pos, state, eventID, eventParam);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity == null ? false : tileentity.receiveClientEvent(eventID, eventParam);
    }
	
	@Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
    {
	    if(playerIn.isSneaking()) {
	        return false;
	    }
	    BlockPos tankPos = new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ());
	    TileEntity teUnknow = worldIn.getTileEntity(tankPos);
	    if(teUnknow instanceof TileTank){
	    	TileTank teTank = (TileTank) worldIn.getTileEntity(tankPos);
		    if(teTank != null){
		    	int liquidAmount = teTank.tank.getFluidAmount();
		    	FluidStack fluid = teTank.tank.getFluid();
		    	int fuildTemp = 0;
		    	if(fluid != null){
		    		fuildTemp = fluid.getFluid().getTemperature();
		    	}
		    	if(worldIn.isRemote){
		    		playerIn.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "Liquid Amount : " + liquidAmount));
		    		playerIn.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "Liquid Temp : " + fuildTemp));
		    	}
		    }
	    }
	    StirlingEngineTileEntity te = (StirlingEngineTileEntity) worldIn.getTileEntity(pos);
	    int energy = te.getEnergyStored(null);
	    if(worldIn.isRemote){
    		playerIn.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "Energy : " + energy + " / " + te.getMaxEnergyStored(null) + " RF"));
    		playerIn.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "-----"));
    	}
        return true;
    }
	
	@Override
	 public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor)
	{
		
	}
	
	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        this.setDefaultFacing(worldIn, pos, state);
    }
	
	private void setDefaultFacing(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!worldIn.isRemote)
        {
            Block block = worldIn.getBlockState(pos.north()).getBlock();
            Block block1 = worldIn.getBlockState(pos.south()).getBlock();
            Block block2 = worldIn.getBlockState(pos.west()).getBlock();
            Block block3 = worldIn.getBlockState(pos.east()).getBlock();
            EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);

            if (enumfacing == EnumFacing.NORTH && block.isFullBlock() && !block1.isFullBlock())
            {
                enumfacing = EnumFacing.SOUTH;
            }
            else if (enumfacing == EnumFacing.SOUTH && block1.isFullBlock() && !block.isFullBlock())
            {
                enumfacing = EnumFacing.NORTH;
            }
            else if (enumfacing == EnumFacing.WEST && block2.isFullBlock() && !block3.isFullBlock())
            {
                enumfacing = EnumFacing.EAST;
            }
            else if (enumfacing == EnumFacing.EAST && block3.isFullBlock() && !block2.isFullBlock())
            {
                enumfacing = EnumFacing.WEST;
            }

            worldIn.setBlockState(pos, state.withProperty(FACING, enumfacing), 2);
        }
    }
	
	@Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
		worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
    }
	
	//Render
	
	@Override
	public boolean isOpaqueCube()
	{
	   return false;
	}
	
	@Override
	public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side)
    {
		return false;
    }
	
	@Override
    public int getRenderType()
    {
        return -1 ;//number 3 for standard block models
    }
	
	@Override
    @SideOnly(Side.CLIENT)
    public IBlockState getStateForEntityRender(IBlockState state)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
    }
	
	@Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        if (hasTileEntity(state)) {
            world.removeTileEntity(pos);
        }
        super.breakBlock(world, pos, state);
    }
}
