package dev.enjarai.trickster.spell.trick.string;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.StringFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

public class DecomposeStringTrick extends Trick<DecomposeStringTrick> {
    public DecomposeStringTrick() {
        super(Pattern.of(4, 8, 6, 3, 0, 2, 4, 3), Signature.of(FragmentType.STRING, DecomposeStringTrick::decompose));
    }

    public Fragment decompose(SpellContext ctx, StringFragment string) throws BlunderException {
        return new ListFragment(string.chars().stream().<Fragment>map(c -> c).toList());
    }
}
