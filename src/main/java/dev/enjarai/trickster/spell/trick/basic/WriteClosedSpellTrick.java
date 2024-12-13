package dev.enjarai.trickster.spell.trick.basic;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.Tricks;
import dev.enjarai.trickster.spell.blunder.BlunderException;

import java.util.List;

public class WriteClosedSpellTrick extends Trick<WriteClosedSpellTrick> {
    public WriteClosedSpellTrick() {
        super(Pattern.of(1, 4, 7, 8, 5, 4, 3, 6, 7, 3, 8, 6, 5, 7));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return Tricks.WRITE_SPELL.activate(ctx, fragments, true);
    }
}
