package dev.enjarai.trickster.spell.trick.list;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.ArgType;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;

public class ListReverseTrick extends DistortionTrick<ListReverseTrick> {
    public ListReverseTrick() {
        super(Pattern.of(2, 0, 3, 6, 8), Signature.of(ArgType.ANY.listOfArg(), ListReverseTrick::reverse, RetType.ANY.listOfRet()));
    }

    public List<Fragment> reverse(SpellContext ctx, List<Fragment> list) {
        return list.reversed();
    }
}
