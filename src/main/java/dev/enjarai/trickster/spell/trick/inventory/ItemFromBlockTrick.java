package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ItemTypeFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.item.Item;

import java.util.List;

public class ItemFromBlockTrick extends Trick {
    public ItemFromBlockTrick() {
        super(Pattern.of(4, 3, 0, 2, 5, 4, 7));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var block = expectInput(fragments, FragmentType.BLOCK_TYPE, 0);
        if (Item.BLOCK_ITEMS.containsKey(block.block())) {
            return new ItemTypeFragment(Item.BLOCK_ITEMS.get(block.block()));
        }
        return VoidFragment.INSTANCE;
    }
}
