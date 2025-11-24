package dev.enjarai.trickster.spell.trick.projectile;

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
        super(Pattern.of(1, 4, 0, 3, 6, 7, 8, 5, 2, 1, 5, 4, 8, 6, 4, 3), Signature.of(FragmentType.VECTOR, FragmentType.SLOT.optionalOfArg(), SummonFireballTrick::run, FragmentType.ENTITY));
    }

    public EntityFragment run(SpellContext ctx, VectorFragment pos, Optional<SlotFragment> optionalSlot) throws BlunderException {
        var stack = ctx.getStack(this, optionalSlot, s -> s.isOf(Items.FIRE_CHARGE)).orElseThrow(() -> new MissingItemBlunder(this));
        var world = ctx.source().getWorld();

        try {
            ctx.useMana(this, cost(ctx.source().getPos().distance(pos.vector())));

            var projectile = EntityType.FIREBALL.create(world);
            projectile.setPos(pos.x(), pos.y(), pos.z());
            ctx.source().getCaster().ifPresent(projectile::setOwner);

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
