package dev.enjarai.trickster.spell.trick.tree;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.ArgType;
import dev.enjarai.trickster.spell.type.Signature;

public class GetWeightTrick extends Trick<GetWeightTrick> {
    public GetWeightTrick() {
        super(Pattern.of(3, 6, 4, 8, 5), Signature.of(ArgType.ANY, GetWeightTrick::run, FragmentType.NUMBER));
    }

    public NumberFragment run(SpellContext ctx, Fragment fragment) {
        return new NumberFragment((double) fragment.getWeight() / Fragment.MAX_WEIGHT);
    }
}
