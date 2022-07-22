package io.github.reoseah.spacefactory.feature.primitive_grinder;

import com.google.common.collect.Lists;
import io.github.reoseah.spacefactory.SpaceFactory;
import io.github.reoseah.spacefactory.common.block.entity.InventoryBlockEntity;
import io.github.reoseah.spacefactory.feature.machine.grinder.GrindingRecipe;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectFloatPair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PrimitiveGrinderBlockEntity extends InventoryBlockEntity implements SidedInventory {
    protected boolean beingRotated = false;
    protected int progress = 0;
    protected int duration = 0;
    private final Object2IntOpenHashMap<Identifier> recipesUsed = new Object2IntOpenHashMap<>();

    public PrimitiveGrinderBlockEntity(BlockPos pos, BlockState state) {
        super(SpaceFactory.BlockEntityTypes.PRIMITIVE_GRINDER, pos, state);
    }

    @Override
    protected DefaultedList<ItemStack> createSlotsList() {
        return DefaultedList.ofSize(1 + 4, ItemStack.EMPTY);
    }

    @Override
    protected Text getDefaultName() {
        return Text.translatable("container.spacefactory.primitive_grinder");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new PrimitiveGrinderScreenHandler.Server(syncId, this, inv);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        this.progress = tag.getInt("Progress");
        int recipesUsedSize = tag.getShort("RecipesUsedSize");
        for (int i = 0; i < recipesUsedSize; ++i) {
            Identifier identifier = new Identifier(tag.getString("RecipeLocation" + i));
            int amount = tag.getInt("RecipeAmount" + i);
            this.recipesUsed.put(identifier, amount);
        }
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.putInt("Progress", this.progress);
        tag.putShort("RecipesUsedSize", (short) this.recipesUsed.size());

        int i = 0;
        for (Map.Entry<Identifier, Integer> entry : this.recipesUsed.entrySet()) {
            tag.putString("RecipeLocation" + i, entry.getKey().toString());
            tag.putInt("RecipeAmount" + i, entry.getValue());
            i++;
        }
    }

    public boolean isBeingRotated() {
        return this.beingRotated;
    }

    public void setBeingRotated(boolean beingRotated) {
        if (this.beingRotated != beingRotated) {
            this.beingRotated = beingRotated;
        }
    }

    public static void tickServer(World world, BlockPos pos, @SuppressWarnings("unused") BlockState state, PrimitiveGrinderBlockEntity be) {
        if (!world.isClient) {
            if (be.isBeingRotated()) {
                GrindingRecipe recipe = world.getRecipeManager().getFirstMatch(SpaceFactory.RecipeTypes.GRINDING, be, world).orElse(null);
                if (recipe != null) {
                    be.progress++;
                    be.duration = recipe.energy / 10;
                    if (be.progress >= recipe.energy / 10) {
                        be.progress = 0;
                        ObjectFloatPair<ItemStack>[] results = recipe.outputs;
                        for (ObjectFloatPair<ItemStack> result : results) {
                            if (world.random.nextFloat() <= result.rightFloat()) {
                                ItemStack excess = be.add(result.left().copy(), 1, 4);
                                if (!excess.isEmpty()) {
                                    Block.dropStack(world, pos.up(), excess);
                                }
                            }
                        }
                        ItemStack input = be.getStack(0);
                        input.decrement(recipe.input.count);
                        be.setStack(0, input);
                        be.recipesUsed.compute(recipe.getId(), (id, integer) -> 1 + (integer == null ? 0 : integer));
                    }
                } else {
                    be.progress = 0;
                }
                be.markDirty();
            }
        }
    }

    public ItemStack add(ItemStack stack, int begin, int end) {
        ItemStack copy = stack.copy();
        this.addToExistingSlot(copy, begin, end);
        if (copy.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            this.addToNewSlot(copy, begin, end);
            return copy.isEmpty() ? ItemStack.EMPTY : copy;
        }
    }

    protected void addToNewSlot(ItemStack stack, int begin, int end) {
        for (int i = begin; i < end; ++i) {
            ItemStack stack2 = this.getStack(i);
            if (stack2.isEmpty()) {
                this.setStack(i, stack.copy());
                stack.setCount(0);
                return;
            }
        }
    }

    protected void addToExistingSlot(ItemStack stack, int begin, int end) {
        for (int i = begin; i < end; ++i) {
            ItemStack stack2 = this.getStack(i);
            if (ItemStack.areItemsEqualIgnoreDamage(stack2, stack)) {
                this.transfer(stack, stack2);
                if (stack.isEmpty()) {
                    return;
                }
            }
        }
    }

    private void transfer(ItemStack source, ItemStack target) {
        int i = Math.min(this.getMaxCountPerStack(), target.getMaxCount());
        int j = Math.min(source.getCount(), i - target.getCount());
        if (j > 0) {
            target.increment(j);
            source.decrement(j);
            this.markDirty();
        }
    }

    public void dropExperienceForRecipesUsed(ServerPlayerEntity player) {
        List<Recipe<?>> list = this.getRecipesUsedAndDropExperience(player.getWorld(), player.getPos());
        player.unlockRecipes(list);
        this.recipesUsed.clear();
    }

    public List<Recipe<?>> getRecipesUsedAndDropExperience(ServerWorld world, Vec3d pos) {
        ArrayList<Recipe<?>> list = Lists.newArrayList();
        for (Object2IntMap.Entry<Identifier> entry : this.recipesUsed.object2IntEntrySet()) {
            world.getRecipeManager().get(entry.getKey()).ifPresent(recipe -> {
                list.add(recipe);
                dropExperience(world, pos, entry.getIntValue(), ((GrindingRecipe) recipe).experience);
            });
        }
        return list;
    }


    private static void dropExperience(ServerWorld world, Vec3d pos, int multiplier, float experience) {
        int size = MathHelper.floor(multiplier * experience);
        float fractionalPart = MathHelper.fractionalPart(multiplier * experience);
        if (fractionalPart != 0.0f && Math.random() < fractionalPart) {
            size++;
        }
        ExperienceOrbEntity.spawn(world, pos, size);
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[]{0, 1, 2, 3};
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        return slot == 0;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot >= 1;
    }
}
