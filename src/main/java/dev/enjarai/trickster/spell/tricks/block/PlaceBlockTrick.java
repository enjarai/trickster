package dev.enjarai.trickster.spell.tricks.block;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

import java.util.List;

public class PlaceBlockTrick extends Trick {
    public PlaceBlockTrick() {
        super(Pattern.of(0, 2, 8, 6, 0));
    }

    //TODO: functions, but could use some clean-up
    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var pos = expectInput(fragments, FragmentType.VECTOR, 0);
        var arg2 = expectInput(fragments, 1);
        var world = ctx.getWorld();
        var blockPos = pos.toBlockPos();
        ItemStack stack = null;
        boolean b = false;

        if (arg2 instanceof SlotFragment slot) {
            stack = slot.getStack(this, ctx);
            b = stack.getItem() instanceof BlockItem;
            if (!b) throw new ItemInvalidBlunder(this);
            slot.move(this, ctx);
        } else {
            var block = expectInput(fragments, FragmentType.BLOCK_TYPE, 1).block();
            var player = ctx.getPlayer().orElseThrow(() -> new NoPlayerBlunder(this));
            var inventory = player.getInventory();

            for (int i = 0; i < inventory.size(); i++) {
                stack = inventory.getStack(i);
                b = stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() == block;
                if (b) break;
            }
        }

        if (stack == null || !b) throw new MissingItemBlunder(this);
        if (!(stack.getItem() instanceof BlockItem blockItem)) throw new ItemInvalidBlunder(this);
        var state = blockItem.getBlock().getDefaultState();

        if (!world.getBlockState(blockPos).isReplaceable() || !state.canPlaceAt(world, blockPos)) {
            throw new CannotPlaceBlockBlunder(this, state.getBlock(), pos);
        }

        var dist = ctx.getPos().distance(pos.vector());
        ctx.useMana(this, (float) (20 + Math.max((dist - 5) * 1.5, 0)));
        stack.decrement(1);
        world.setBlockState(blockPos, state);

        return VoidFragment.INSTANCE;
    }
}
