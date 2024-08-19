package dev.enjarai.trickster.spell.trick.entity;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.cca.ModEntityCumponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.blunder.InvalidEntityBlunder;
import dev.enjarai.trickster.spell.trick.blunder.UnknownEntityBlunder;
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

        fragments = tryWard(ctx, target, fragments);

        var scaleFragment = expectInput(fragments, FragmentType.NUMBER, 1);

        var scale = MathHelper.clamp(scaleFragment.number(), 0.0625, 8.0) - 1;
        if (!(target instanceof LivingEntity livingEntity)) {
            throw new InvalidEntityBlunder(this);
        }

        var currentScale = 0d;
        if (livingEntity.getAttributes().hasModifierForAttribute(EntityAttributes.GENERIC_SCALE, SCALE_ID)) {
            currentScale = livingEntity.getAttributes().getModifierValue(EntityAttributes.GENERIC_SCALE, SCALE_ID);
        }

        var difference = Math.abs(scale - currentScale);
        ctx.useMana(this, (float) (difference * difference * 10 + scale * 50));
        livingEntity.getAttributes().getCustomInstance(EntityAttributes.GENERIC_SCALE)
                .overwritePersistentModifier(new EntityAttributeModifier(SCALE_ID, scale, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        ModEntityCumponents.GRACE.get(livingEntity).triggerGrace("scale", 100);

        return BooleanFragment.TRUE;
    }
}
