package dev.enjarai.trickster.spell.trick.projectile;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.MissingItemBlunder;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import java.util.Optional;

public class SummonFireballTrick extends Trick<SummonFireballTrick> {
    public SummonFireballTrick() {
        super(Pattern.of(1, 4, 0, 3, 6, 7, 8, 5, 2, 1, 5, 4, 8, 6, 4, 3), Signature.of(FragmentType.VECTOR, FragmentType.SLOT.optionalOf(), SummonFireballTrick::run));
    }

    public Fragment run(SpellContext ctx, VectorFragment pos, Optional<SlotFragment> optionalSlot) throws BlunderException {
        var stack = ctx.getStack(this, optionalSlot, s -> s.isOf(Items.FIRE_CHARGE)).orElseThrow(() -> new MissingItemBlunder(this));
        var world = ctx.source().getWorld();

        try {
            expectCanBuild(ctx, pos.toBlockPos());

            ctx.useMana(this, cost(ctx.source().getPos().distance(pos.vector())));

            var projectile = EntityType.FIREBALL.create(world);
            projectile.setPos(pos.x(), pos.y(), pos.z());

            world.spawnEntity(projectile);
            return EntityFragment.from(projectile);
        } catch (Throwable err) {
            ctx.source().offerOrDropItem(stack);
            throw err;
        }
    }

    private float cost(double dist) {
        return (float) (20 + Math.pow(dist, (dist / 3)));
    }
}
