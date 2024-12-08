package dev.enjarai.trickster.item.recipe;

import com.google.common.collect.ImmutableMap;
import dev.enjarai.trickster.item.ModItems;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Map;
import java.util.stream.Stream;

public class ScrollDyeingRecipe extends SpecialCraftingRecipe {
    private final Item original;
    private final Identifier recipeSerializer;
    private final Map<DyeColor, Item> dyeMap;
    private final Ingredient ingredient;

    public ScrollDyeingRecipe(CraftingRecipeCategory craftingRecipeCategory, Item original, Identifier recipeSerializer) {
        super(craftingRecipeCategory);
        this.original = original;
        this.recipeSerializer = recipeSerializer;

        var dyeMap = ImmutableMap.<DyeColor, Item>builder();
        for (var variant : ModItems.DYED_VARIANTS) {
            if (variant.original() == original) {
                dyeMap.put(variant.color(), variant.variant());
            }
        }
        this.dyeMap = dyeMap.build();

        this.ingredient = Ingredient.ofItems(Stream.concat(
                Stream.of(original),
                ModItems.DYED_VARIANTS.stream().filter(v -> v.original() == original).map(ModItems.DyedVariant::variant)
        ));
    }

    public boolean matches(CraftingRecipeInput craftingRecipeInput, World world) {
        int i = 0;
        int j = 0;

        for(int k = 0; k < craftingRecipeInput.size(); ++k) {
            ItemStack itemStack = craftingRecipeInput.getStackInSlot(k);
            if (!itemStack.isEmpty()) {
                if (ingredient.test(itemStack)) {
                    ++i;
                } else {
                    if (!(itemStack.getItem() instanceof DyeItem)) {
                        return false;
                    }

                    ++j;
                }

                if (j > 1 || i > 1) {
                    return false;
                }
            }
        }

        return i == 1 && j == 1;
    }

    public ItemStack craft(CraftingRecipeInput craftingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
        ItemStack itemStack = ItemStack.EMPTY;
        DyeItem dyeItem = (DyeItem) Items.WHITE_DYE;

        for(int i = 0; i < craftingRecipeInput.size(); ++i) {
            ItemStack itemStack2 = craftingRecipeInput.getStackInSlot(i);
            if (!itemStack2.isEmpty()) {
                Item item = itemStack2.getItem();
                if (ingredient.test(itemStack2)) {
                    itemStack = itemStack2;
                } else if (item instanceof DyeItem) {
                    dyeItem = (DyeItem) item;
                }
            }
        }

        var item = dyeMap.get(dyeItem.getColor());
        return itemStack.copyComponentsToNewStack(item, 1);
    }

    @Override
    public RecipeSerializer<? extends SpecialCraftingRecipe> getSerializer() {
        return (RecipeSerializer<? extends SpecialCraftingRecipe>) Registries.RECIPE_SERIALIZER.get(recipeSerializer);
    }
}

