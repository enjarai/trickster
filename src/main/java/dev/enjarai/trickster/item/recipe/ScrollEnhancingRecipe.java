package dev.enjarai.trickster.item.recipe;

import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.WrittenScrollItem;
import dev.enjarai.trickster.item.component.ModComponents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class ScrollEnhancingRecipe extends SpecialCraftingRecipe {
    public ScrollEnhancingRecipe(CraftingRecipeCategory craftingRecipeCategory) {
        super(craftingRecipeCategory);
    }

    public boolean matches(CraftingRecipeInput craftingRecipeInput, World world) {
        int i = 0;
        ItemStack itemStack = ItemStack.EMPTY;

        for(int j = 0; j < craftingRecipeInput.getSize(); ++j) {
            ItemStack itemStack2 = craftingRecipeInput.getStackInSlot(j);
            if (!itemStack2.isEmpty()) {
                if (itemStack2.isOf(ModItems.WRITTEN_SCROLL)) {
                    if (!itemStack.isEmpty()) {
                        return false;
                    }

                    itemStack = itemStack2;
                } else {
                    if (!itemStack2.isOf(ModItems.SPELL_INK)) {
                        return false;
                    }

                    ++i;
                }
            }
        }

        return !itemStack.isEmpty() && i > 0;
    }

    public ItemStack craft(CraftingRecipeInput craftingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
        int i = 0;
        ItemStack itemStack = ItemStack.EMPTY;

        for(int j = 0; j < craftingRecipeInput.getSize(); ++j) {
            ItemStack itemStack2 = craftingRecipeInput.getStackInSlot(j);
            if (!itemStack2.isEmpty()) {
                if (itemStack2.isOf(ModItems.WRITTEN_SCROLL)) {
                    if (!itemStack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }

                    itemStack = itemStack2;
                } else {
                    if (!itemStack2.isOf(ModItems.SPELL_INK)) {
                        return ItemStack.EMPTY;
                    }

                    ++i;
                }
            }
        }

        var meta = itemStack.get(ModComponents.WRITTEN_SCROLL_META);
        var spell = itemStack.get(ModComponents.SPELL);
        if (!itemStack.isEmpty() && i > 0 && meta != null && spell != null) {
            var newMeta = meta.withExecutable(i * 100);
            ItemStack itemStack3 = itemStack.copyWithCount(1);
            itemStack3.set(ModComponents.WRITTEN_SCROLL_META, newMeta);
            itemStack3.set(ModComponents.SPELL, spell);
            return itemStack3;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SCROLL_ENHANCING_RECIPE;
    }

    @Override
    public boolean fits(int width, int height) {
        return width >= 2 && height >= 2;
    }
}
