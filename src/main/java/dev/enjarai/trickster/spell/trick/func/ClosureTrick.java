package dev.enjarai.trickster.spell.trick.func;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.ArgType;
import dev.enjarai.trickster.spell.type.Signature;
import io.vavr.collection.HashMap;

public class ClosureTrick extends DistortionTrick<ClosureTrick> {
    public ClosureTrick() {
        super(Pattern.of(5, 8, 7, 6, 3, 0, 1), Signature.of(FragmentType.SPELL_PART, ArgType.ANY.mappedTo(ArgType.ANY), ClosureTrick::run, FragmentType.SPELL_PART));
    }

    public SpellPart run(SpellContext ctx, SpellPart spell, HashMap<Fragment, Fragment> map) {
        return spell.deepClone().buildClosure(map);
    }
}
