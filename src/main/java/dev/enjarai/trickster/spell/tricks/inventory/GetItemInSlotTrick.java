package dev.enjarai.trickster.spell.tricks.inventory;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ItemTypeFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;

import java.util.List;

public class GetItemInSlotTrick extends Trick {
    public GetItemInSlotTrick() {
        super(Pattern.of(1, 4, 7, 8, 5, 2, 4, 6, 3, 4));
    }

    @Override
    public Fragment activate(SpellSource ctx, List<Fragment> fragments) throws BlunderException {
        return new ItemTypeFragment(expectInput(fragments, FragmentType.SLOT, 0).getItem(this, ctx));
    }
}