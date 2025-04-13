package dev.enjarai.trickster.spell.trick.entity;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.InvalidEntityBlunder;
import dev.enjarai.trickster.spell.blunder.UnknownEntityBlunder;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

public class SetScaleTrick extends Trick<SetScaleTrick> {
    public SetScaleTrick() {
        super(Pattern.of(7, 6, 0, 1, 2, 8, 7, 4), Signature.of(FragmentType.ENTITY.wardOf(), FragmentType.NUMBER, SetScaleTrick::set));
    }

    public Fragment set(SpellContext ctx, EntityFragment entity, NumberFragment scaleFragment) throws BlunderException {
        var target = entity.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this));

        var scale = MathHelper.clamp(scaleFragment.number(), 0.0625, 2.0);
        if (!(target instanceof LivingEntity livingEntity)) {
            throw new InvalidEntityBlunder(this);
        }

        var scaleComponent = ModEntityComponents.SCALE.get(target);
        var currentScale = scaleComponent.getScale();

        var difference = Math.abs(scale - currentScale);
        ctx.useMana(this, (float) (difference * difference * 100 + scale * 50));
        scaleComponent.setScale(scale);
        ModEntityComponents.GRACE.get(livingEntity).triggerGrace("scale", 100);

        return EntityFragment.from(target);
    }
}
