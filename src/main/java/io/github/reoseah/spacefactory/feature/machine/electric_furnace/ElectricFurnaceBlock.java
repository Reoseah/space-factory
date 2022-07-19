package io.github.reoseah.spacefactory.feature.machine.electric_furnace;

import io.github.reoseah.spacefactory.feature.machine.AbstractMachineBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class ElectricFurnaceBlock extends AbstractMachineBlock {
    public ElectricFurnaceBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }
}
