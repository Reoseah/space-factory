package io.github.reoseah.spacefactory.common.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public abstract class InventoryBlockEntity extends BlockEntity implements Inventory, Nameable, NamedScreenHandlerFactory {
    protected final DefaultedList<ItemStack> slots;
    protected Text customName;

    public InventoryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.slots = this.createSlotsList();
    }

    protected abstract DefaultedList<ItemStack> createSlotsList();

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        this.slots.clear();
        Inventories.readNbt(tag, this.slots);
        if (tag.contains("CustomName", NbtElement.STRING_TYPE)) {
            this.customName = Text.Serializer.fromJson(tag.getString("CustomName"));
        }
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        Inventories.writeNbt(tag, this.slots);
        if (this.customName != null) {
            tag.putString("CustomName", Text.Serializer.toJson(this.customName));
        }
    }

    // region Inventory
    @Override
    public int size() {
        return this.slots.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.slots) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.slots.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.slots, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.slots, slot);
    }

    @Override
    public void setStack(int slot, ItemStack newStack) {
        this.slots.set(slot, newStack);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean canPlayerUse(PlayerEntity player) {
        return this.world.getBlockEntity(this.pos) == this && player.squaredDistanceTo(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) <= 64;
    }

    @Override
    public void clear() {
        this.slots.clear();
    }
    // endregion


    protected boolean canAcceptStack(int slot, ItemStack offer) {
        ItemStack stackInSlot = this.getStack(slot);
        if (stackInSlot.isEmpty() || offer.isEmpty()) {
            return true;
        }
        return ItemStack.canCombine(stackInSlot, offer)
                && stackInSlot.getCount() + offer.getCount() <= Math.min(stackInSlot.getMaxCount(), this.getMaxCountPerStack());
    }

    protected void acceptStack(int slot, ItemStack stack) {
        ItemStack stackInSlot = this.getStack(slot);
        if (stackInSlot.isEmpty()) {
            this.setStack(slot, stack);
        } else if (stackInSlot.getItem() == stack.getItem()) {
            stackInSlot.increment(stack.getCount());
        }
        this.markDirty();
    }

    // region Nameable & NamedScreenHandlerFactory
    public void setCustomName(Text customName) {
        this.customName = customName;
    }

    @Override
    public Text getName() {
        if (this.customName != null) {
            return this.customName;
        }
        return this.getDefaultName();
    }

    @Override
    public Text getDisplayName() {
        return this.getName();
    }

    @Override
    @Nullable
    public Text getCustomName() {
        return this.customName;
    }

    protected abstract Text getDefaultName();
    // endregion
}
