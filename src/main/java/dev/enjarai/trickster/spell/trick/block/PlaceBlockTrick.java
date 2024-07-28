package dev.enjarai.trickster.spell.trick.block;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.*;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class PlaceBlockTrick extends Trick {
    public PlaceBlockTrick() {
        super(Pattern.of(0, 2, 8, 6, 0));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var pos = expectInput(fragments, FragmentType.VECTOR, 0);
        var arg2 = expectInput(fragments, 1);
        var world = ctx.source().getWorld();
        var blockPos = pos.toBlockPos();
        ItemStack stack;

        if (arg2 instanceof SlotFragment slot)
            stack = ctx.getStack(this, Optional.of(slot), item -> item instanceof BlockItem);
        else {
            var block = expectInput(fragments, FragmentType.BLOCK_TYPE, 1).block();
            stack = ctx.getStack(this, Optional.empty(), item -> item instanceof BlockItem blockItem && blockItem.getBlock() == block);
        }

        try {
            if (!(stack.getItem() instanceof BlockItem blockItem)) throw new ItemInvalidBlunder(this);
            var state = blockItem.getBlock().getDefaultState();

            if (!world.getBlockState(blockPos).isReplaceable() || !state.canPlaceAt(world, blockPos)) {
                throw new CannotPlaceBlockBlunder(this, state.getBlock(), pos);
            }

            var dist = ctx.source().getPos().distance(pos.vector());
            ctx.useMana(this, (float) (20 + Math.max((dist - 5) * 1.5, 0)));
            world.setBlockState(blockPos, state);

            return VoidFragment.INSTANCE;
        } catch (BlunderException blunder) {
            var thisPos = ctx.source().getPos();
            world.spawnEntity(new ItemEntity(world, thisPos.x, thisPos.y, thisPos.z, stack));
            throw blunder;
        }
    }
}
