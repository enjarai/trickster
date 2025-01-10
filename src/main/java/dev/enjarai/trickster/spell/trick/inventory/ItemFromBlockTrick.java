package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.BlockTypeFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ItemTypeFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.item.Item;

public class ItemFromBlockTrick extends Trick<ItemFromBlockTrick> {
    public ItemFromBlockTrick() {
        super(Pattern.of(4, 3, 0, 2, 5, 4, 7), Signature.of(FragmentType.BLOCK_TYPE, ItemFromBlockTrick::run));
    }

    public Fragment run(SpellContext ctx, BlockTypeFragment blockType) throws BlunderException {
        if (Item.BLOCK_ITEMS.containsKey(blockType.block())) {
            return new ItemTypeFragment(Item.BLOCK_ITEMS.get(blockType.block()));
        }

        return VoidFragment.INSTANCE;
    }
}
