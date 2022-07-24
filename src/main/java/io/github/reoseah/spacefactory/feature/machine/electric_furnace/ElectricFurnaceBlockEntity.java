package io.github.reoseah.spacefactory.feature.machine.electric_furnace;

import io.github.reoseah.spacefactory.SpaceFactory;
import io.github.reoseah.spacefactory.common.block.entity.InventoryBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "OptionalAssignedToNull"})
public class ElectricFurnaceBlockEntity extends InventoryBlockEntity implements SidedInventory {
    protected int energy;
    protected @Nullable Optional<SmeltingRecipe> lastRecipe = null;
    protected int progress;

    public ElectricFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(SpaceFactory.BlockEntityTypes.ELECTRIC_FURNACE, pos, state);
    }

    @Override
    protected DefaultedList<ItemStack> createSlotsList() {
        return DefaultedList.ofSize(ElectricFurnaceProps.INVENTORY_SIZE, ItemStack.EMPTY);
    }

    @Override
    public Text getDefaultName() {
        return Text.translatable("container.spacefactory.electric_furnace");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new ElectricFurnaceScreenHandler.Server(syncId, this, playerInventory);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        this.energy = tag.getInt("Energy");
        this.progress = tag.getInt("Progress");
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.putInt("Energy", this.energy);
        tag.putInt("Progress", this.progress);
    }

    @Override
    public void setStack(int slot, ItemStack newStack) {
        if (slot == ElectricFurnaceProps.INPUT_SLOT) {
            ItemStack current = this.slots.get(slot);

            if (newStack.isEmpty() || current.isEmpty() || !ItemStack.canCombine(current, newStack)) {
                this.lastRecipe = null;
                this.progress = 0;
            }
        }
        super.setStack(slot, newStack);
    }

    public static void tickServer(World world, @SuppressWarnings("unused") BlockPos pos, @SuppressWarnings("unused") BlockState state, ElectricFurnaceBlockEntity be) {
        // FIXME test
        be.energy = ElectricFurnaceProps.CAPACITY / 2;

        if (be.lastRecipe == null) {
            be.lastRecipe = world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, be, world);
        }
        SmeltingRecipe recipe = be.lastRecipe != null ? be.lastRecipe.orElse(null) : null;

        if (recipe != null && be.canAcceptRecipeOutput(recipe)) {
            if (be.energy >= 1) {
                int progress = Math.min(recipe.getCookTime() * 10 - be.progress, Math.min(be.energy, ElectricFurnaceProps.CONSUMPTION));
                be.progress += progress;
                be.energy -= progress;
                if (be.progress >= recipe.getCookTime() * 10) {
                    be.craftRecipe(recipe);
                    be.progress = 0;
                }
                be.markDirty();
            }
        } else if (be.progress > 0) {
            be.progress = 0;
            be.markDirty();
        }
    }

    protected boolean canAcceptRecipeOutput(SmeltingRecipe recipe) {
        return this.canAcceptStack(ElectricFurnaceProps.OUTPUT_SLOT, recipe.getOutput());
    }

    protected void craftRecipe(SmeltingRecipe recipe) {
        ItemStack input = this.slots.get(ElectricFurnaceProps.INPUT_SLOT);
        input.decrement(1);
        // also updates cached recipe if needed
        this.setStack(ElectricFurnaceProps.INPUT_SLOT, input);

        this.acceptStack(ElectricFurnaceProps.OUTPUT_SLOT, recipe.craft(this));
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return ElectricFurnaceProps.SLOTS;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return slot == ElectricFurnaceProps.INPUT_SLOT;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot == ElectricFurnaceProps.OUTPUT_SLOT;
    }
}
