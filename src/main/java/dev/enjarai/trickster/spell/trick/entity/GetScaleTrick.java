package dev.enjarai.trickster.spell.trick.entity;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.InvalidEntityBlunder;
import dev.enjarai.trickster.spell.blunder.UnknownEntityBlunder;
import net.minecraft.entity.LivingEntity;

import java.util.List;

public class GetScaleTrick extends Trick {
    public GetScaleTrick() {
        super(Pattern.of(7, 6, 0, 1, 2, 8, 7));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var target = expectInput(fragments, FragmentType.ENTITY, 0)
                .getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this));

        if (!(target instanceof LivingEntity)) {
            throw new InvalidEntityBlunder(this);
        }

        var currentScale = ModEntityComponents.SCALE.get(target).getScale();
        return new NumberFragment(currentScale);
    }
}
