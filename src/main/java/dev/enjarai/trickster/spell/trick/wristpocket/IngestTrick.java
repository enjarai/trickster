package dev.enjarai.trickster.spell.trick.wristpocket;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.Trick;

import java.util.List;

public class IngestTrick extends Trick {
    public IngestTrick() {
        super(Pattern.of(7, 4, 3, 0, 4, 2, 5, 4));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {

        return fragments.getFirst();
    }
}
