package dev.enjarai.trickster.item.recipe;

import dev.enjarai.trickster.item.ScrollAndQuillItem;
import dev.enjarai.trickster.item.WrittenScrollItem;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.item.component.FragmentComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class ScrollCloningRecipe extends SpecialCraftingRecipe {
    public ScrollCloningRecipe(CraftingRecipeCategory craftingRecipeCategory) {
        super(craftingRecipeCategory);
    }

    public boolean matches(CraftingRecipeInput craftingRecipeInput, World world) {
        int i = 0;
        ItemStack itemStack = ItemStack.EMPTY;

        for(int j = 0; j < craftingRecipeInput.getSize(); ++j) {
            ItemStack itemStack2 = craftingRecipeInput.getStackInSlot(j);
            if (!itemStack2.isEmpty()) {
                if (itemStack2.getItem() instanceof WrittenScrollItem && itemStack2.get(ModComponents.FRAGMENT) instanceof FragmentComponent spell && !spell.closed()) {
                    if (!itemStack.isEmpty()) {
                        return false;
                    }

                    itemStack = itemStack2;
                } else {
                    if (!(itemStack2.getItem() instanceof ScrollAndQuillItem)) {
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
                if (itemStack2.getItem() instanceof WrittenScrollItem) {
                    if (!itemStack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }

                    itemStack = itemStack2;
                } else {
                    if (!(itemStack2.getItem() instanceof ScrollAndQuillItem)) {
                        return ItemStack.EMPTY;
                    }

                    ++i;
                }
            }
        }

        var meta = itemStack.get(ModComponents.WRITTEN_SCROLL_META);
        var spell = itemStack.get(ModComponents.FRAGMENT);
        if (!itemStack.isEmpty() && i >= 1 && meta != null && spell != null) {
            var newMeta = meta.copy();
            if (newMeta == null) {
                return ItemStack.EMPTY;
            } else {
                ItemStack itemStack3 = itemStack.copyWithCount(i);
                itemStack3.set(ModComponents.WRITTEN_SCROLL_META, newMeta);
                itemStack3.set(ModComponents.FRAGMENT, spell);
                return itemStack3;
            }
        } else {
            return ItemStack.EMPTY;
        }
    }

    public DefaultedList<ItemStack> getRemainder(CraftingRecipeInput craftingRecipeInput) {
        DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(craftingRecipeInput.getSize(), ItemStack.EMPTY);

        for(int i = 0; i < defaultedList.size(); ++i) {
            ItemStack itemStack = craftingRecipeInput.getStackInSlot(i);
            if (itemStack.getItem().hasRecipeRemainder()) {
                defaultedList.set(i, new ItemStack(itemStack.getItem().getRecipeRemainder()));
            } else if (itemStack.getItem() instanceof WrittenScrollItem) {
                defaultedList.set(i, itemStack.copyWithCount(1));
                break;
            }
        }

        return defaultedList;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SCROLL_CLONING_RECIPE;
    }

    @Override
    public boolean fits(int width, int height) {
        return width >= 2 && height >= 2;
    }
}
