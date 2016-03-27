package tinker_io.TileEntity.fim;

import java.util.ArrayList;
import java.util.List;

import tinker_io.TileEntity.TileEntityContainerAdapter;
import tinker_io.api.Observable;
import tinker_io.api.Observer;
import tinker_io.registry.ItemRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class FIMTileEntity extends TileEntityContainerAdapter implements ITickable, Observable<Observer>
{
    private static final int[] slotsSpeedUPG = new int[] { 0 };
    private static final int[] slotsFuel = new int[] { 1 };
    private static final int[] slotsUPG1 = new int[] { 2 };
    private static final int[] slotsUPG2 = new int[] { 3 };


    public FIMTileEntity()
    {
        super(null, 4);
    }

    public ItemStack fuel = new ItemStack(ItemRegistry.SolidFuel);
    private List<Observer> obs = new ArrayList();

    protected SCInfo scInfo;
    protected FuelFSM fuelFSM = FuelFSMFactory.getNewFuelFSM(this);


    @Override
    public void onLoad()
    {
        scInfo = SCInfoFactory.getSmelyeryControllerInfo(this);
        fuelFSM.init();
    }

    protected int tick = 0;

    public int keepInputTime;
    public int inputTime;
    public int fuelTemp;


    @Override
    public void update()
    {
        if (!this.worldObj.isRemote)
        {
            if (tick % 4 == 0)
            {
                toUpdateSCInfoAndSpeedUpSC();
            }
            tick = (tick + 1) % 20;
        }
    }


    private void toUpdateSCInfoAndSpeedUpSC()
    {
        this.scInfo.update();
        this.fuelFSM.update();

        if (scInfo.canFindSCPos() && scInfo.isSCHeatingItem())
        {
            fuelFSM.startChangeState();
            Adapter adap = scInfo.getAdapter();
            
            final int fuelTemp = adap.getFuelTemp();

            this.toSpeedUpSC(fuelTemp, adap);
        }

        if (fuelFSM.isActive)
        {
            this.notifyObservers();
        }
    }


    private void toSpeedUpSC(final int originFuelTemp, Adapter adap)
    {
        int f = fuelTemp / 2 + originFuelTemp;

        adap.setFuelTemp(f);
    }


    public int getSpeedUpTemp(final int originFuelTemp)
    {
        //		return getStackSize(this.getSlots()[0]) * 200 + originFuelTemp;
        return TileEntityFurnace.getItemBurnTime(this.getSlots()[1]) + originFuelTemp;
    }


    public void onNeighborChange(IBlockAccess world, BlockPos neighbor)
    {
        scInfo.manager.onNeighborChange(world, neighbor);
    }


    public int getStackSize(ItemStack stack)
    {
        return stack == null ? 0 : stack.stackSize;
    }


    @Override
    public void addObserver(Observer o)
    {
        this.obs.add(o);
    }


    @Override
    public void removeObserver(Observer o)
    {
        int index = this.obs.indexOf(o);
        if (index >= 0)
        {
            obs.remove(index);
        }
    }


    @Override
    public void notifyObservers()
    {
        for (Observer o : this.obs)
        {
            o.receivedTopic();
        }
    }


    /**
     * Returns true if automation is allowed to insert the given stack
     * (ignoring stack size) into the given slot.
     */
    @Override
    public boolean isItemValidForSlot(int index, ItemStack itemstack)
    {
        if (itemstack.isItemEqual(fuel))
        {
            return true;
        }
        return false;
    }


    @Override
    public int[] getSlotsForFace(EnumFacing side)
    {
        return slotsFuel;
    }


    /**
     * Returns true if automation can insert the given item in the given slot from the given side.
     * Args: slot, item, side
     */
    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction)
    {
        return this.isItemValidForSlot(index, itemStackIn);
    }


    /**
     * Returns true if automation can extract the given item in the given slot from the given side.
     * Args: slot, item, side
     */
    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction)
    {
        //return par3 != 0 || par1 != 1 || itemstack.getItem() == Items.bucket;
        return false;
    }


    /**
     *  loading and saving
     */

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);
        this.inputTime = tag.getShort("InputTime");
        this.keepInputTime = tag.getShort("keepInputTime");

        this.fuelFSM.readFromNBT(tag);
    }


    @Override
    public void writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);
        tag.setShort("InputTime", (short) this.inputTime);
        tag.setShort("keepInputTime", (short) this.keepInputTime);

        this.fuelFSM.writeToNBT(tag);
    }


    /*
     * GUI
     */

    @SideOnly(Side.CLIENT)
    public int getCookProgressScaled(int pixels)
    {
        final int n = (this.inputTime < 0) ? 0 : this.inputTime;

        int i = this.keepInputTime;
        if (i == 0)
        {
            i = 1000;
        }

        return n * pixels / i;
    }


    public boolean hasFuel()
    {
        if (slots[1] != null && this.getSlots()[1].isItemEqual(fuel))
        {
            return true;
        }
        return false;
    }


    public SpeedUpRatio getSpeedUpInfo()
    {
        double temp = this.getStackSize(slots[0]) * 0.2 + 1.0;
        return new SpeedUpRatio(temp);
    }


    @SideOnly(Side.CLIENT)
    public String getDirection()
    {
        return this.scInfo.getFacing();
    }


    public int getInputTime()
    {
        return this.inputTime;
    }
    /*
     * Data
     */

    public static class SpeedUpRatio
    {
        public double ratio;


        public SpeedUpRatio(double temp)
        {
            this.ratio = temp;
        }
    }
}