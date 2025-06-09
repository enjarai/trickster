package dev.enjarai.trickster.spell.trick.misc;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.NoPlayerBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.Optional;

public class ShowBarTrick extends Trick<ShowBarTrick> {
    public ShowBarTrick() {
        super(Pattern.of(3, 0, 6, 3, 4, 5, 2, 8, 5), Signature.of(FragmentType.NUMBER, FragmentType.NUMBER, FragmentType.NUMBER.optionalOf(), ShowBarTrick::run, FragmentType.NUMBER));
    }

    public NumberFragment run(SpellContext ctx, NumberFragment id, NumberFragment value, Optional<NumberFragment> optionalMaxValue) throws BlunderException {
        double maxValue = optionalMaxValue.map(NumberFragment::number).orElse(1d);

        if (maxValue == 0) {
            maxValue = 1.0;
        }

        ctx.source().getPlayer().orElseThrow(() -> new NoPlayerBlunder(this))
                .getComponent(ModEntityComponents.BARS).setBar(id.asInt(), value.number() / maxValue);

        return value;
    }
}
