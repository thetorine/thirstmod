package com.thetorine.thirstmod.common.blocks;

import com.thetorine.thirstmod.Constants;
import com.thetorine.thirstmod.ThirstMod;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockDrinksStore extends BlockContainer {

    public static final PropertyDirection FACING = BlockHorizontal.FACING;

    public BlockDrinksStore() {
        super(Material.ROCK);
        setUnlocalizedName("drinks_store");
        setRegistryName(Constants.MOD_ID, "drinks_store");
        setHardness(3.5F);
        setSoundType(SoundType.STONE);
        setCreativeTab(CreativeTabs.FOOD);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) return true;
        TileEntity entity = worldIn.getTileEntity(pos);
        if (entity instanceof TileEntityDrinksStore) {
            playerIn.openGui(ThirstMod.getInstance(), Constants.DRINKS_STORE_ID, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity entity = worldIn.getTileEntity(pos);
        if (entity instanceof TileEntityDrinksStore) {
            InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityDrinksStore)entity);
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(ThirstMod.getProxy().DRINKS_STORE);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(ThirstMod.getProxy().DRINKS_STORE);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityDrinksStore();
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {FACING});
    }
}
