package dev.enjarai.trickster.spell.trick.tree;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;

import java.util.List;

public class EscapePatternTrick extends Trick {
    public EscapePatternTrick() {
        super(Pattern.of(1, 5, 7, 3, 1, 4, 3));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var pattern = expectInput(fragments, FragmentType.PATTERN, 0);
        return pattern.pattern();
    }
}
