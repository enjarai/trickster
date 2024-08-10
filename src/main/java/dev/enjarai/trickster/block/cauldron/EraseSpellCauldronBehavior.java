package dev.enjarai.trickster.block.cauldron;

import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.component.ModComponents;
import net.minecraft.block.BlockState;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EraseSpellCauldronBehavior implements CauldronBehavior {
    @Override
    public ItemActionResult interact(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack) {
        stack.remove(ModComponents.WRITTEN_SCROLL_META);
        stack.remove(ModComponents.SPELL);
        player.setStackInHand(hand, stack.withItem(ModItems.SCROLL_AND_QUILL));
        return ItemActionResult.SUCCESS;
    }
}
