package io.github.reoseah.spacefactory.feature.primitive_grinder;

import io.github.reoseah.spacefactory.SpaceFactory;
import io.github.reoseah.spacefactory.common.block.entity.Renameable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PrimitiveGrinderBlock extends BlockWithEntity {
    public PrimitiveGrinderBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PrimitiveGrinderBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : checkType(type, SpaceFactory.BlockEntityTypes.PRIMITIVE_GRINDER, PrimitiveGrinderBlockEntity::tickServer);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        BlockEntity entity = world.getBlockEntity(pos);
        if (stack.hasCustomName() && entity instanceof Renameable inventory) {
            inventory.setCustomName(stack.getName());
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof PrimitiveGrinderBlockEntity grinder) {
                ItemScatterer.spawn(world, pos, grinder);
                grinder.getRecipesUsedAndDropExperience((ServerWorld) world, Vec3d.ofCenter(pos));
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            NamedScreenHandlerFactory factory = state.createScreenHandlerFactory(world, pos);
            if (factory != null) {
                player.openHandledScreen(factory);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos, boolean moved) {
        if (world.getBlockState(pos.up()).getBlock() != SpaceFactory.Blocks.CRANK
                || !(world.getBlockEntity(pos.up()) instanceof CrankBlockEntity crank)
                || crank.ticksToRotate <= 0) {
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof PrimitiveGrinderBlockEntity grinder) {
                grinder.setBeingRotated(false);
            }
        }
        super.neighborUpdate(state, world, pos, block, neighborPos, moved);
    }
}
