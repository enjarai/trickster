package dev.enjarai.trickster.spell.trick.misc;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.NoPlayerBlunder;

import java.util.List;

public class ShowBarTrick extends Trick {
    public ShowBarTrick() {
        super(Pattern.of(3, 0, 6, 3, 4, 5, 2, 8, 5));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var id = expectInput(fragments, FragmentType.NUMBER, 0);
        var value = expectInput(fragments, FragmentType.NUMBER, 1);
        double maxValue = supposeInput(fragments, FragmentType.NUMBER, 2)
                .map(NumberFragment::number).orElse(1d);

        if (maxValue == 0) {
            maxValue = 1.0;
        }

        ctx.source().getPlayer().orElseThrow(() -> new NoPlayerBlunder(this))
                .getComponent(ModEntityComponents.BARS).setBar(id.asInt(), value.number() / maxValue);

        return value;
    }
}
