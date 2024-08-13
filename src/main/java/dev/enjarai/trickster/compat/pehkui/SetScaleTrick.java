package dev.enjarai.trickster.compat.pehkui;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.blunder.UnknownEntityBlunder;
import net.minecraft.util.math.MathHelper;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.List;

public class SetScaleTrick extends Trick {
    public SetScaleTrick() {
        super(Pattern.of(7, 6, 0, 1, 2, 8, 7, 4));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var target = expectInput(fragments, FragmentType.ENTITY, 0).getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this));

        fragments = tryWard(ctx, target, fragments);

        var scale = MathHelper.clamp(expectInput(fragments, FragmentType.NUMBER, 1).number(), 0.0625, 5.0);
        var scaleData = ScaleTypes.BASE.getScaleData(target);

        var difference = Math.abs(scale - scaleData.getScale());
        ctx.useMana(this, (float) (difference * difference * 10 + scale * 50));
        scaleData.setScale((float) scale);

        return BooleanFragment.TRUE;
    }
}
