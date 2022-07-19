package io.github.reoseah.spacefactory.feature.machine.grinder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.reoseah.spacefactory.SpaceFactory;
import io.github.reoseah.spacefactory.common.recipe.IngredientCount;
import it.unimi.dsi.fastutil.objects.ObjectFloatPair;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class GrindingRecipe implements Recipe<Inventory> {
    protected final Identifier id;
    public final IngredientCount input;
    protected final ObjectFloatPair<ItemStack>[] outputs;
    public final int energy;

    public GrindingRecipe(Identifier id, IngredientCount input, ObjectFloatPair<ItemStack>[] outputs, int energy) {
        this.id = id;
        this.input = input;
        this.outputs = outputs;
        this.energy = energy;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        return this.input.test(inventory.getStack(0));
    }

    @Override
    public ItemStack craft(Inventory inventory) {
        return this.getOutput().copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput() {
        return this.outputs[0].left();
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SpaceFactory.RecipeSerializers.GRINDING;
    }

    @Override
    public RecipeType<?> getType() {
        return SpaceFactory.RecipeTypes.GRINDING;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return DefaultedList.copyOf(Ingredient.EMPTY, this.input.ingredient);
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<GrindingRecipe> {
        @Override
        public GrindingRecipe read(Identifier identifier, JsonObject json) {
            IngredientCount input = IngredientCount.fromJson(json.get("ingredient"));
            int energy = JsonHelper.getInt(json, "energy", 2000);

            ObjectFloatPair<ItemStack>[] outputs;
            if (JsonHelper.hasJsonObject(json, "output")) {
                JsonObject outputJson = json.getAsJsonObject("output");
                ItemStack stack = ShapedRecipe.outputFromJson(outputJson);
                float chance = JsonHelper.getFloat(outputJson, "chance", 1.0F);
                outputs = new ObjectFloatPair[]{ObjectFloatPair.of(stack, chance)};
            } else {
                JsonArray outputJson = JsonHelper.getArray(json, "output");
                outputs = new ObjectFloatPair[outputJson.size()];
                for (int i = 0; i < outputs.length; i++) {
                    JsonObject output = outputJson.get(i).getAsJsonObject();
                    ItemStack stack = ShapedRecipe.outputFromJson(output);
                    float chance = JsonHelper.getFloat(output, "chance", 1.0F);
                    outputs[i] = ObjectFloatPair.of(stack, chance);
                }
            }

            return new GrindingRecipe(identifier, input, outputs, energy);
        }

        @Override
        public GrindingRecipe read(Identifier identifier, PacketByteBuf buf) {
            IngredientCount input = IngredientCount.fromPacket(buf);
            int energy = buf.readVarInt();
            int outputCount = buf.readVarInt();
            ObjectFloatPair<ItemStack>[] outputs = new ObjectFloatPair[outputCount];
            for (int i = 0; i < outputCount; i++) {
                ItemStack stack = buf.readItemStack();
                float chance = buf.readFloat();
                outputs[i] = ObjectFloatPair.of(stack, chance);
            }

            return new GrindingRecipe(identifier, input, outputs, energy);
        }

        @Override
        public void write(PacketByteBuf buf, GrindingRecipe recipe) {
            recipe.input.write(buf);
            buf.writeVarInt(recipe.energy);
            buf.writeVarInt(recipe.outputs.length);
            for (ObjectFloatPair<ItemStack> output : recipe.outputs) {
                buf.writeItemStack(output.left());
                buf.writeFloat(output.rightFloat());
            }
        }
    }
}
