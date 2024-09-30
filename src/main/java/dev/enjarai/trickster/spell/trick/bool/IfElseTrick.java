package dev.enjarai.trickster.spell.trick.bool;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;

import java.util.List;

public class IfElseTrick extends Trick {
    public IfElseTrick() {
        super(Pattern.of(3, 4, 0, 2, 4, 5));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var check = expectInput(fragments, 0);
        var params1 = expectInput(fragments, 1);
        var params2 = expectInput(fragments, 2);

        if (check.asBoolean()) {
            return params1;
        } else {
            return params2;
        }
    }
}
