package dev.enjarai.trickster.spell.trick.entity;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.InvalidEntityBlunder;
import dev.enjarai.trickster.spell.blunder.UnknownEntityBlunder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class SetScaleTrick extends Trick {
    public static final Identifier SCALE_ID = Trickster.id("scale");

    public SetScaleTrick() {
        super(Pattern.of(7, 6, 0, 1, 2, 8, 7, 4));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var target = expectInput(fragments, FragmentType.ENTITY, 0).getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this));
        var scaleFragment = expectInput(fragments, FragmentType.NUMBER, 1);
        tryWard(ctx, target, fragments);

        var scale = MathHelper.clamp(scaleFragment.number(), 0.0625, 8.0);
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
