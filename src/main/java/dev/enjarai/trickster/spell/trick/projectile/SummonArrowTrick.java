package dev.enjarai.trickster.spell.trick.projectile;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.MissingItemBlunder;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.slot.SlotFragment;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.item.ProjectileItem;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public class SummonArrowTrick extends Trick<SummonArrowTrick> {
    public SummonArrowTrick() {
        super(Pattern.of(2, 5, 1, 2, 4, 3, 6, 4, 7, 6), Signature.of(FragmentType.VECTOR, FragmentType.SLOT.optionalOfArg(), SummonArrowTrick::run, FragmentType.ENTITY));
    }

    public EntityFragment run(SpellContext ctx, VectorFragment pos, Optional<SlotFragment> optionalSlot) {
        var stack = ctx.getStack(this, optionalSlot, s -> s.isIn(ItemTags.ARROWS) && s.getItem() instanceof ProjectileItem).orElseThrow(() -> new MissingItemBlunder(this));
        var world = ctx.source().getWorld();

        try {
            ctx.useMana(this, cost(ctx.source().getPos().distance(pos.vector())));

            var projectile = ((ProjectileItem) stack.getItem()).createEntity(ctx.source().getWorld(), new Vec3d(0, 0, 0).fromVector3d(pos.vector()), stack, Direction.DOWN);

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
