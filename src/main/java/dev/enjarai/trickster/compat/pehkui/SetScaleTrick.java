package dev.enjarai.trickster.compat.pehkui;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.UnknownEntityBlunder;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.List;

public class SetScaleTrick extends Trick {
    public SetScaleTrick() {
        super(Pattern.of(7, 6, 0, 1, 2, 8, 7, 4));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var target = expectInput(fragments, FragmentType.ENTITY, 0);
        var scale = expectInput(fragments, FragmentType.NUMBER, 1);

        var scaleData = ScaleTypes.BASE.getScaleData(target.getEntity(ctx)
                        .orElseThrow(() -> new UnknownEntityBlunder(this)));

        var difference = Math.abs(scale.number() - scaleData.getScale());
        ctx.useMana(this, (float) (difference * difference * 10));
        scaleData.setScale((float) scale.number());

        return BooleanFragment.TRUE;
    }
}
