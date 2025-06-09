package dev.enjarai.trickster.spell.trick.list;

import com.google.common.collect.ImmutableList;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.ArgType;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;

public class ListAddRangeTrick extends DistortionTrick<ListAddRangeTrick> {
    public ListAddRangeTrick() {
        super(Pattern.of(6, 0, 4, 6, 3, 0, 2, 5, 8), Signature.of(ArgType.ANY.listOfArg(), ArgType.ANY.listOfArg().variadicOfArg(), ListAddRangeTrick::add, RetType.ANY.listOfRet()));
    }

    public List<Fragment> add(SpellContext ctx, List<Fragment> baseList, List<List<Fragment>> lists) throws BlunderException {
        var builder = ImmutableList.<Fragment>builder();
        builder.addAll(baseList);

        for (var list : lists) {
            builder.addAll(list);
        }

        return builder.build();
    }
}
