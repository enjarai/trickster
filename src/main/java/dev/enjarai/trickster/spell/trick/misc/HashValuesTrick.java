package dev.enjarai.trickster.spell.trick.misc;

import java.util.List;
import java.util.Objects;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;

public class HashValuesTrick extends Trick {
	public HashValuesTrick() {
		super(Pattern.of(1, 4, 8, 7, 4, 3));
	}

	@Override
	public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
		return new NumberFragment(fragments.stream()
            .map(Fragment::applyEphemeral)
            .map(Fragment::hashCode)
            .reduce(0, (left, right) -> Objects.hash(left, right)));
	}

}
