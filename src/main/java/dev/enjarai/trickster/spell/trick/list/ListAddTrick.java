package dev.enjarai.trickster.spell.trick.list;

import com.google.common.collect.ImmutableList;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;

public class ListAddTrick extends DistortionTrick<ListAddTrick> {
    public ListAddTrick() {
        super(Pattern.of(0, 4, 6, 3, 0, 2, 5, 8), Signature.of(list(Fragment.class), ANY_VARIADIC, ListAddTrick::run, RetType.ANY.listOf()));
    }

    public List<Fragment> run(SpellContext ctx, List<Fragment> list, List<Fragment> toAdd) throws BlunderException {
        return ImmutableList.<Fragment>builder().addAll(list).addAll(toAdd).build();
    }
}
