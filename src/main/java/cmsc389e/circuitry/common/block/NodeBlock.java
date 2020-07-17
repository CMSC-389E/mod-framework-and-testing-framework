package cmsc389e.circuitry.common.block;

import java.util.List;

import cmsc389e.circuitry.common.Config;
import cmsc389e.circuitry.common.NodeTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public abstract class NodeBlock extends Block {
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public static void setPowered(World world, BlockState state, BlockPos pos, boolean powered) {
		if (state.get(POWERED) != powered)
			world.setBlockState(pos, state.cycle(POWERED));
	}

	public NodeBlock(String name) {
		super(Properties.create(Material.IRON));
		setDefaultState(getDefaultState().with(POWERED, false));
		setRegistryName(name);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new NodeTileEntity();
	}

	@Override
	public void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(POWERED);
	}

	@Deprecated
	@Override
	public int getLightValue(BlockState state) {
		return state.get(POWERED) ? Config.LIGHT.get() : 0;
	}

	public abstract List<String> getNodeTags();

	public abstract String getPrefix();

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	/**
	 * Called when a {@link InNodeBlock} is right-clicked and the
	 * {@link PlayerEntity} is not crouching. The {@link World} is not checked
	 * whether it is remote since all this method does is modify
	 * {@link NodeTileEntity#tag} at the {@link BlockPos}. On {@link Chunk} load,
	 * the client-side {@link NodeTileEntity} is synced to the server-side one. That
	 * means {@link NodeTileEntity#tag} will always be synchronized.
	 *
	 * @param state   the {@link BlockState} of the {@link InNodeBlock} that was
	 *                clicked
	 * @param worldIn the {@link World} that the {@link InNodeBlock} was clicked in
	 * @param pos     the {@link BlockPos} of the {@link InNodeBlock} that was
	 *                clicked
	 * @param player  the {@link PlayerEntity} who did the clicking
	 * @param handIn  the {@link Hand} with which the {@link PlayerEntity} clicked
	 * @param hit     where on the {@link InNodeBlock}'s bounds it was hit
	 * @return {@link ActionResultType#SUCCESS}, which tells the game that the
	 *         action was consumed correctly
	 * @deprecated
	 */
	@Deprecated
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
		((NodeTileEntity) worldIn.getTileEntity(pos)).setTag(true);
		return ActionResultType.SUCCESS;
	}
}