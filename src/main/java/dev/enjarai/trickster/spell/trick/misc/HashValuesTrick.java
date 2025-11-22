package dev.enjarai.trickster.spell.trick.misc;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.ArgType;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;
import java.util.Objects;

public class HashValuesTrick extends Trick<HashValuesTrick> {
    public HashValuesTrick() {
        super(Pattern.of(1, 4, 8, 7, 4, 3), Signature.of(ArgType.ANY.variadicOfArg(), HashValuesTrick::run, FragmentType.NUMBER));
    }

    public NumberFragment run(SpellContext ctx, List<Fragment> args) {
        return new NumberFragment(args.stream()
                .map(Fragment::applyEphemeral)
                .map(Fragment::fuzzyHash)
                .reduce(0, Objects::hash));
    }
}
