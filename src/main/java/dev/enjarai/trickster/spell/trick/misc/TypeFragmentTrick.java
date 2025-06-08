package dev.enjarai.trickster.spell.trick.misc;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.TypeFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;

public class TypeFragmentTrick extends Trick<TypeFragmentTrick> {
    public TypeFragmentTrick() {
        super(Pattern.of(3, 4, 0, 1, 4, 5), Signature.of(ANY, TypeFragmentTrick::run, FragmentType.TYPE));
    }

    public TypeFragment run(SpellContext ctx, Fragment input) throws BlunderException {
        return new TypeFragment(input.type());
    }
}
