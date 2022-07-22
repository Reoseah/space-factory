package io.github.reoseah.spacefactory.feature.machine.electric_furnace;

import io.github.reoseah.spacefactory.SpaceFactory;
import io.github.reoseah.spacefactory.feature.machine.AbstractMachineBlock;
import io.github.reoseah.spacefactory.feature.machine.grinder.GrinderBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ElectricFurnaceBlock extends AbstractMachineBlock {
    public ElectricFurnaceBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ElectricFurnaceBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : checkType(type, SpaceFactory.BlockEntityTypes.ELECTRIC_FURNACE, ElectricFurnaceBlockEntity::tickServer);
    }
}
