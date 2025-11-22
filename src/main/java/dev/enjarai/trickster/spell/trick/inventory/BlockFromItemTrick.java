package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BlockTypeFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ItemTypeFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.item.BlockItem;

import java.util.Optional;

public class BlockFromItemTrick extends Trick<BlockFromItemTrick> {
    public BlockFromItemTrick() {
        super(Pattern.of(4, 3, 6, 8, 5, 4, 1), Signature.of(FragmentType.ITEM_TYPE, BlockFromItemTrick::run, FragmentType.BLOCK_TYPE.optionalOfRet()));
    }

    public Optional<BlockTypeFragment> run(SpellContext ctx, ItemTypeFragment itemType) {
        if (itemType.item() instanceof BlockItem blockItem) {
            return Optional.of(new BlockTypeFragment(blockItem.getBlock()));
        }

        return Optional.empty();
    }
}
