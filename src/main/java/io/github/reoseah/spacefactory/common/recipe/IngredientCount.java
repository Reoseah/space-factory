package io.github.reoseah.spacefactory.common.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * Basically, {@link Ingredient} & integer count.
 * <p>
 * Created for machines that consume multiple items per recipe, which is a common occurrence in tech mods.
 * <p>
 * Doesn't support arrays currently, use item tags instead if multiple inputs are needed.
 */
public class IngredientCount implements Predicate<ItemStack> {
    public final Ingredient ingredient;
    public final int count;

    public IngredientCount(Ingredient ingredient, int count) {
        this.ingredient = ingredient;
        this.count = count;
    }

    @Override
    public boolean test(ItemStack stack) {
        return this.ingredient.test(stack) && stack.getCount() >= this.count;
    }

    public static IngredientCount fromJson(@Nullable JsonElement json) {
        if (json == null || json.isJsonNull()) {
            throw new JsonSyntaxException("Item entry cannot be null");
        }
        int count = json.isJsonObject() ? JsonHelper.getInt(json.getAsJsonObject(), "count", 1) : 0;
        if (count <= 0) {
            throw new JsonSyntaxException("Item count must be greater than 0");
        }
        Ingredient ingredient = Ingredient.fromJson(json);
        return new IngredientCount(ingredient, count);
    }

    public void write(PacketByteBuf buf) {
        this.ingredient.write(buf);
        buf.writeInt(this.count);
    }

    public static IngredientCount fromPacket(PacketByteBuf buf) {
        return new IngredientCount(Ingredient.fromPacket(buf), buf.readInt());
    }
}
