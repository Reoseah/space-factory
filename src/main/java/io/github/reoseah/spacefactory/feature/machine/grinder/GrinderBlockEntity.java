package io.github.reoseah.spacefactory.feature.machine.grinder;

import io.github.reoseah.spacefactory.SpaceFactory;
import io.github.reoseah.spacefactory.common.block.entity.InventoryBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "OptionalAssignedToNull"})
public class GrinderBlockEntity extends InventoryBlockEntity implements SidedInventory {
    protected int energy;
    protected @Nullable Optional<GrindingRecipe> lastRecipe = null;
    protected int progress;

    public GrinderBlockEntity(BlockPos pos, BlockState state) {
        super(SpaceFactory.BlockEntityTypes.GRINDER, pos, state);
    }

    @Override
    protected DefaultedList<ItemStack> createSlotsList() {
        return DefaultedList.ofSize(GrinderProps.INVENTORY_SIZE, ItemStack.EMPTY);
    }

    @Override
    public Text getDefaultName() {
        return Text.translatable("container.spacefactory.grinder");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new GrinderScreenHandler.Server(syncId, this, playerInventory);
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
        if (slot == GrinderProps.INPUT_SLOT) {
            ItemStack current = this.slots.get(slot);

            boolean lessThenCurrentRecipe = this.lastRecipe != null && this.lastRecipe.isPresent() && this.lastRecipe.get().input.count > newStack.getCount();

            if (newStack.isEmpty() || current.isEmpty() || !ItemStack.canCombine(current, newStack) || lessThenCurrentRecipe) {
                this.lastRecipe = null;
                this.progress = 0;
            }
        }
        super.setStack(slot, newStack);
    }

    public static void tickServer(World world, @SuppressWarnings("unused") BlockPos pos, @SuppressWarnings("unused") BlockState state, GrinderBlockEntity be) {
        // FIXME test
        be.energy = GrinderProps.CAPACITY / 2;

        boolean wasActive = state.get(Properties.LIT);
        boolean active = false;

        if (be.lastRecipe == null) {
            be.lastRecipe = world.getRecipeManager().getFirstMatch(SpaceFactory.RecipeTypes.GRINDING, be, world);
        }
        GrindingRecipe recipe = be.lastRecipe != null ? be.lastRecipe.orElse(null) : null;

        if (recipe != null && be.canAcceptRecipeOutput(recipe)) {
            if (be.energy >= 1) {
                active = true;
                int progress = Math.min(recipe.energy - be.progress, Math.min(be.energy, GrinderProps.CONSUMPTION));
                be.progress += progress;
                be.energy -= progress;
                if (be.progress >= recipe.energy) {
                    be.craftRecipe(recipe);
                    be.progress = 0;
                }
                be.markDirty();
            } else {
                active = false;
            }
        } else if (be.progress > 0) {
            be.progress = 0;
            active = false;
            be.markDirty();
        }
        if (wasActive != active) {
            world.setBlockState(pos, state.with(Properties.LIT, active), 3);
        }
    }

    protected boolean canAcceptRecipeOutput(GrindingRecipe recipe) {
        for (int i = 0; i < recipe.outputs.length && i < 4; i++) {
            if (!this.canAcceptStack(GrinderProps.OUTPUT_SLOTS_START + i, recipe.outputs[i].left())) {
                return false;
            }
        }
        return true;
    }

    protected void craftRecipe(GrindingRecipe recipe) {
        ItemStack input = this.slots.get(GrinderProps.INPUT_SLOT);
        input.decrement(recipe.input.count);
        // also updates cached recipe if needed
        this.setStack(GrinderProps.INPUT_SLOT, input);

        for (int i = 0; i < recipe.outputs.length && i < 4; i++) {
            if (Objects.requireNonNull(this.world).random.nextFloat() <= recipe.outputs[i].rightFloat()) {
                this.acceptStack(GrinderProps.OUTPUT_SLOTS_START + i, recipe.outputs[i].left().copy());
            }
        }
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return GrinderProps.SLOTS;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return slot == GrinderProps.INPUT_SLOT;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot >= GrinderProps.OUTPUT_SLOTS_START;
    }
}
