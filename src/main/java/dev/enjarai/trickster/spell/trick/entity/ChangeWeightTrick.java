package dev.enjarai.trickster.spell.trick.entity;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.entity.LevitatingBlockEntity;
import dev.enjarai.trickster.particle.ModParticles;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.*;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.entity.LivingEntity;

public class ChangeWeightTrick extends Trick<ChangeWeightTrick> {
    public ChangeWeightTrick() {
        super(Pattern.of(0, 3, 6, 7, 4, 1, 2, 5, 8), Signature.of(FragmentType.ENTITY.wardOf(), FragmentType.NUMBER, ChangeWeightTrick::change));
        overload(Signature.of(FragmentType.VECTOR, FragmentType.NUMBER, ChangeWeightTrick::change));
    }

    public Fragment change(SpellContext ctx, EntityFragment target, NumberFragment number) throws BlunderException {
        var entity = target
                .getEntity(ctx)
                .orElseThrow(() -> new UnknownEntityBlunder(this));
        var weight = number.number();

        if (weight > 1) {
            throw new NumberTooLargeBlunder(this, 1);
        } else if (weight < 0) {
            throw new NumberTooSmallBlunder(this, 0);
        }

        if (entity instanceof LevitatingBlockEntity levitatingBlock) {
            ctx.useMana(this, (float) (60 * (1 - weight)));

            levitatingBlock.setWeight((float) weight);
            ModEntityComponents.GRACE.get(entity).triggerGrace("weight", 20);
        } else {
            if (!(entity instanceof LivingEntity)) {
                throw new EntityInvalidBlunder(this);
            }

            ctx.useMana(this, (float) (60 * (1 - weight)));

            ModEntityComponents.WEIGHT.get(entity).setWeight(weight);
            ModEntityComponents.GRACE.get(entity).triggerGrace("weight", 20);
        }

        return target;
    }

    public Fragment change(SpellContext ctx, VectorFragment target, NumberFragment number) throws BlunderException {
        var state = ctx.source().getWorld().getBlockState(target.toBlockPos());
        var weight = number.number();

        if (weight > 1) {
            throw new NumberTooLargeBlunder(this, 1);
        } else if (weight < 0) {
            throw new NumberTooSmallBlunder(this, 0);
        }

        if (state.isAir()) {
            throw new BlockInvalidBlunder(this);
        }

        ctx.useMana(this, (float) (60 * (1 - weight)));

        var levitatingBlock = LevitatingBlockEntity.spawnFromBlock(
                ctx.source().getWorld(), target.toBlockPos(), state, (float) weight);
        ModEntityComponents.GRACE.get(levitatingBlock).triggerGrace("weight", 20);

        var particlePos = target.toBlockPos().toCenterPos();
        ctx.source().getWorld().spawnParticles(
                ModParticles.PROTECTED_BLOCK, particlePos.x, particlePos.y, particlePos.z,
                1, 0, 0, 0, 0);

        return EntityFragment.from(levitatingBlock);
    }
}
