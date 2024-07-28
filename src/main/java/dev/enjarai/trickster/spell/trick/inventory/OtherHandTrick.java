package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.ItemTypeFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.blunder.MissingItemBlunder;

import java.util.List;

public class OtherHandTrick extends Trick {
    public OtherHandTrick() {
        super(Pattern.of(7, 4, 1, 0, 3, 4));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return new ItemTypeFragment(ctx.source().getOtherHandSpellStack()
                .orElseThrow(() -> new MissingItemBlunder(this)).getItem());
    }
}
