package dev.enjarai.trickster.item.recipe;

import dev.enjarai.trickster.Trickster;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModRecipes {
    public static final RecipeSerializer<ScrollCloningRecipe> SCROLL_CLONING_RECIPE =
            Registry.register(Registries.RECIPE_SERIALIZER, Trickster.id("scroll_cloning"), new SpecialRecipeSerializer<>(ScrollCloningRecipe::new));

    public static void register() {

    }
}
