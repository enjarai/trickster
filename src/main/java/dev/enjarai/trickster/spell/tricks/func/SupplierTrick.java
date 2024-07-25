package dev.enjarai.trickster.spell.tricks.func;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;

import java.util.List;

public class SupplierTrick extends Trick {
    public SupplierTrick() {
        super(Pattern.of(0, 1, 2, 5, 8, 7, 6, 3, 0));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var fragment = expectInput(fragments, 0);

        var spell = new SpellPart();
        spell.glyph = fragment;

        return spell;
    }
}
