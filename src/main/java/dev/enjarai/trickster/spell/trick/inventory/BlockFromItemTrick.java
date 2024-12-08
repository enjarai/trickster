package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.BlockTypeFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.item.BlockItem;

import java.util.List;

public class BlockFromItemTrick extends Trick {
    public BlockFromItemTrick() {
        super(Pattern.of(4, 3, 6, 8, 5, 4, 1));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var item = expectInput(fragments, FragmentType.ITEM_TYPE, 0);
        if (item.item() instanceof BlockItem blockItem) {
            return new BlockTypeFragment(blockItem.getBlock());
        }
        return VoidFragment.INSTANCE;
    }
}
