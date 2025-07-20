package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ItemTypeFragment;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

public class GetMaxCount extends Trick<GetMaxCount> {
    public GetMaxCount() {
        super(Pattern.of(1, 0, 2, 1, 4, 5, 8, 2, 5, 7, 6, 3, 1), Signature.of(FragmentType.ITEM_TYPE, GetMaxCount::run, FragmentType.NUMBER));
    }

    public NumberFragment run(SpellContext ctx, ItemTypeFragment item) throws BlunderException {
        return new NumberFragment(item.item().getMaxCount());
    }
}
