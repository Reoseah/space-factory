package io.github.reoseah.spacefactory.feature.tool;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface Wrenchable {
    /**
     * @return whether player should swing a wrench/consume wrench durability/etc
     */
    boolean useWrench(BlockState state, World world, BlockPos pos, Direction side, @Nullable PlayerEntity player, Hand hand, Vec3d hitPos);

    /**
     * Shorthand for casting block to {@link Wrenchable} and if successful calling {@link #useWrench}.
     */
    static boolean tryWrench(BlockState state, World world, BlockPos pos, Direction side, @Nullable PlayerEntity player, Hand hand, Vec3d hitPos) {
        if (state.getBlock() instanceof Wrenchable wrenchable) {
            return wrenchable.useWrench(state, world, pos, side, player, hand, hitPos);
        }
        return false;
    }
}