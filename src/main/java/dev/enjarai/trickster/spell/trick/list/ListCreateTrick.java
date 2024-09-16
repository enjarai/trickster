package dev.enjarai.trickster.spell.trick.list;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;

import java.util.List;

/**
 * Continues to exist for backwards compatibility, users should instead use the ConstantRevision with the same pattern.
 */
public class ListCreateTrick extends Trick {
    public ListCreateTrick() {
        super(Pattern.of(6, 3, 0, 2, 5, 8));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return new ListFragment(List.of());
    }
}
