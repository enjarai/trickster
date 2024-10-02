package dev.enjarai.trickster.item.recipe;

import dev.enjarai.trickster.item.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class EchoManaCrystalRecipe extends SpecialCraftingRecipe {
    public EchoManaCrystalRecipe(CraftingRecipeCategory craftingRecipeCategory) {
        super(craftingRecipeCategory);
    }

    public boolean matches(CraftingRecipeInput craftingRecipeInput, World world) {
        int i = 0;
        ItemStack itemStack = ItemStack.EMPTY;

        //TODO: this is horrible
        for(int j = 0; j < craftingRecipeInput.getSize(); ++j) {
            ItemStack itemStack2 = craftingRecipeInput.getStackInSlot(j);
            
            if (itemStack2.isOf(Items.ECHO_SHARD)) return true;
        }

        return false;
    }

    public ItemStack craft(CraftingRecipeInput craftingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
        return ModItems.ECHO_MANA_CRYSTAL.makePair();
    }

    public DefaultedList<ItemStack> getRemainder(CraftingRecipeInput craftingRecipeInput) {
        return DefaultedList.ofSize(craftingRecipeInput.getSize(), ItemStack.EMPTY);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.ECHO_MANA_CRYSTAL_RECIPE;
    }

    @Override
    public boolean fits(int width, int height) {
        return width >= 2 && height >= 2;
    }
}
