package vazkii.quark.decoration.block;

import it.unimi.dsi.fastutil.floats.Float2ObjectArrayMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemGroup;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IEnviromentBlockReader;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.module.Module;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GrateBlock extends QuarkBlock implements IWaterLoggable {

	private static final VoxelShape TRUE_SHAPE = makeCuboidShape(0, 15, 0, 16, 16, 16);
	private static final VoxelShape SPAWN_BLOCK_SHAPE = makeCuboidShape(0, 15, 0, 1, 17, 1);
	private static final VoxelShape SELECTION_SHAPE;
	private static final Float2ObjectArrayMap<VoxelShape> WALK_BLOCK_CACHE = new Float2ObjectArrayMap<>();

	public static BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	static {
		VoxelShape shape = VoxelShapes.empty();

		for (int x = 0; x < 4; x++)
			shape = VoxelShapes.or(shape, makeCuboidShape(1 + x * 4, 15, 0, 3 + x * 4, 16, 16));
		for (int z = 0; z < 4; z++)
			shape = VoxelShapes.or(shape, makeCuboidShape(0, 15, 1 + z * 4, 16, 16, 3 + z * 4));

		SELECTION_SHAPE = shape;
	}

	public GrateBlock(String regname, Module module, ItemGroup creativeTab, Properties properties) {
		super(regname, module, creativeTab, properties);

		setDefaultState(getDefaultState().with(WATERLOGGED, false));
	}

	private static VoxelShape createNewBox(double height) {
		return makeCuboidShape(0, 15, 0, 16, 16 + height * 16, 16);
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return SELECTION_SHAPE;
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, ISelectionContext context) {
		Entity entity = context.getEntity();

		if(entity != null && !(entity instanceof ItemEntity)) {
			if (entity instanceof AnimalEntity)
				return WALK_BLOCK_CACHE.computeIfAbsent(entity.stepHeight, GrateBlock::createNewBox);

			if (!(entity instanceof PlayerEntity))
				return SPAWN_BLOCK_SHAPE;

			return TRUE_SHAPE;
		}

		return VoxelShapes.empty();
	}

	@Nullable
	@Override
	public PathNodeType getAiPathNodeType(BlockState state, IBlockReader world, BlockPos pos, @Nullable MobEntity entity) {
		if (entity instanceof AnimalEntity)
			return PathNodeType.DAMAGE_OTHER;
		return null;
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public IFluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean allowsMovement(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, PathType path) {
		return false;
	}

	@Override
	public boolean doesSideBlockRendering(BlockState state, IEnviromentBlockReader world, BlockPos pos, Direction side) {
		if (side.getAxis() == Direction.Axis.Y)
			return super.doesSideBlockRendering(state, world, pos, side);

		BlockState stateAt = world.getBlockState(pos.offset(side));
		Block block = stateAt.getBlock();

		return block != this && super.doesSideBlockRendering(stateAt, world, pos, side);
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos) {
		return !state.get(WATERLOGGED);
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean causesSuffocation(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos) {
		return super.causesSuffocation(state, world, pos);
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isNormalCube(BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos) {
		return super.isNormalCube(state, world, pos);
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean canEntitySpawn(BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, EntityType<?> type) {
		return false;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED);
	}
}
