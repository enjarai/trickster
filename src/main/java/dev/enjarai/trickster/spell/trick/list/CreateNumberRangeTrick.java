package dev.enjarai.trickster.spell.trick.list;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;
import java.util.stream.IntStream;

public class CreateNumberRangeTrick extends Trick<CreateNumberRangeTrick> {
    public CreateNumberRangeTrick() {
        super(Pattern.of(1, 2, 0, 1, 4, 7, 8, 6, 7), Signature.of(FragmentType.NUMBER, FragmentType.NUMBER, CreateNumberRangeTrick::run, FragmentType.NUMBER.listOfRet()));
    }

    public List<NumberFragment> run(SpellContext ctx, NumberFragment start, NumberFragment end) {
        return IntStream.range(start.asInt(), end.asInt()).boxed().map(NumberFragment::new).toList();
    }
}
