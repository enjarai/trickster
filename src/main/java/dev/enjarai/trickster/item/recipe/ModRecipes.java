package dev.enjarai.trickster.item.recipe;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.item.ModItems;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModRecipes {
    public static final RecipeSerializer<ScrollCloningRecipe> SCROLL_CLONING_RECIPE =
            Registry.register(Registries.RECIPE_SERIALIZER, Trickster.id("scroll_cloning"), new SpecialCraftingRecipe.SpecialRecipeSerializer<>(ScrollCloningRecipe::new));
    public static final RecipeSerializer<ScrollEnhancingRecipe> SCROLL_ENHANCING_RECIPE =
            Registry.register(Registries.RECIPE_SERIALIZER, Trickster.id("scroll_enhancing"), new SpecialCraftingRecipe.SpecialRecipeSerializer<>(ScrollEnhancingRecipe::new));
    public static final RecipeSerializer<ScrollDyeingRecipe> SCROLL_AND_QUILL_DYEING =
            Registry.register(Registries.RECIPE_SERIALIZER, Trickster.id("scroll_and_quill_dyeing"),
                    new SpecialCraftingRecipe.SpecialRecipeSerializer<>(c -> new ScrollDyeingRecipe(c, ModItems.SCROLL_AND_QUILL, Trickster.id("scroll_and_quill_dyeing"))));
    public static final RecipeSerializer<ScrollDyeingRecipe> WRITTEN_SCROLL_DYEING =
            Registry.register(Registries.RECIPE_SERIALIZER, Trickster.id("written_scroll_dyeing"),
                    new SpecialCraftingRecipe.SpecialRecipeSerializer<>(c -> new ScrollDyeingRecipe(c, ModItems.WRITTEN_SCROLL, Trickster.id("written_scroll_dyeing"))));

    public static void register() {

    }
}
