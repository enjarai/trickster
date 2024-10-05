package dev.enjarai.trickster.spell.trick.mana;

import java.util.List;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;

public class LinkToEchoTrick extends Trick {
	public LinkToEchoTrick() {
		super(Pattern.of(7, 4, 1, 0, 4, 2, 1, 3, 4, 5, 1, 6, 7, 8, 1));
	}

	@Override
	public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return VoidFragment.INSTANCE;
	}

}
