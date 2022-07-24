package io.github.reoseah.spacefactory.feature.tool;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MacheteItem extends AxeItem {
    public MacheteItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return !miner.isCreative();
    }

    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        if (state.isOf(Blocks.COBWEB)) {
            return 15.0F;
        }
        Material material = state.getMaterial();
        float swordSpeed = material == Material.PLANT || material == Material.REPLACEABLE_PLANT || state.isIn(BlockTags.LEAVES) || material == Material.GOURD ? 1.5F : 1.0F;
        return Math.max(super.getMiningSpeedMultiplier(stack, state), swordSpeed);
    }


    public boolean isSuitableFor(BlockState state) {
        return state.isOf(Blocks.COBWEB) || super.isSuitableFor(state);
    }
}
