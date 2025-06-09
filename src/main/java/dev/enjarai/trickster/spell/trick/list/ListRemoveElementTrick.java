package dev.enjarai.trickster.spell.trick.list;

import com.google.common.collect.ImmutableList;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.ArrayList;
import java.util.List;

public class ListRemoveElementTrick extends DistortionTrick<ListRemoveElementTrick> {
    public ListRemoveElementTrick() {
        super(Pattern.of(4, 6, 3, 0, 4, 8, 5, 2), Signature.of(list(Fragment.class), ANY_VARIADIC, ListRemoveElementTrick::run, RetType.ANY.listOf()));
    }

    public List<Fragment> run(SpellContext ctx, List<Fragment> list, List<Fragment> toRemove) throws BlunderException {
        var newList = new ArrayList<Fragment>(list.size());
        newList.addAll(list);
        newList.removeAll(toRemove);
        return ImmutableList.copyOf(newList);
    }
}
