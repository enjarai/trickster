package dev.enjarai.trickster.compat.pehkui;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.blunder.UnknownEntityBlunder;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.List;

public class GetScaleTrick extends Trick {
    public GetScaleTrick() {
        super(Pattern.of(7, 6, 0, 1, 2, 8, 7));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var target = expectInput(fragments, FragmentType.ENTITY, 0);

        return new NumberFragment(ScaleTypes.BASE.getScaleData(target.getEntity(ctx)
                .orElseThrow(() -> new UnknownEntityBlunder(this))).getScale());
    }
}
