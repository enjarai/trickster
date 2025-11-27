package dev.enjarai.trickster.spell.trick.misc;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.TypeFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.ArgType;
import dev.enjarai.trickster.spell.type.Signature;

public class TypeFragmentTrick extends Trick<TypeFragmentTrick> {
    public TypeFragmentTrick() {
        super(Pattern.of(3, 4, 0, 1, 4, 5), Signature.of(ArgType.ANY, TypeFragmentTrick::run, FragmentType.TYPE));
    }

    public TypeFragment run(SpellContext ctx, Fragment input) {
        return new TypeFragment(input.type());
    }
}
