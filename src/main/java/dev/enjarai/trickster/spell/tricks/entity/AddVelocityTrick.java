package dev.enjarai.trickster.spell.tricks.entity;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;

import java.util.List;

public class AddVelocityTrick extends Trick {
    public AddVelocityTrick() {
        super(Pattern.of(1, 2)); // TODO
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var pos = expectInput(fragments, FragmentType.VECTOR, 0);
        var velocity = expectInput(fragments, FragmentType.VECTOR, 1);

        return null;
    }
}
