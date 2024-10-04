package dev.enjarai.trickster.spell.trick.projectile;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.MissingItemBlunder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.util.List;

public abstract class AbstractProjectileTrick extends Trick {
    public AbstractProjectileTrick(Pattern pattern) {
        super(pattern);
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var pos = expectInput(fragments, FragmentType.VECTOR, 0).vector();
        var optionalSlot = supposeInput(fragments, FragmentType.SLOT, 1);
        var stack = ctx.getStack(this, optionalSlot, this::isValidItem).orElseThrow(() -> new MissingItemBlunder(this));
        var world = ctx.source().getWorld();

        try {
            ctx.useMana(this, cost(ctx.source().getPos().distance(pos)));

            var projectile = makeProjectile(ctx, pos, stack, fragments.subList(optionalSlot.isPresent() ? 2 : 1, fragments.size()));
            world.spawnEntity(projectile);

            return EntityFragment.from(projectile);
        } catch (BlunderException blunder) {
            onFail(ctx, world, ctx.source().getPos(), pos, stack);
            throw blunder;
        }
    }

    protected abstract Entity makeProjectile(SpellContext ctx, Vector3dc pos, ItemStack stack, List<Fragment> extraInputs) throws BlunderException;

    protected abstract boolean isValidItem(Item item);

    protected void onFail(SpellContext ctx, ServerWorld world, Vector3d spellPos, Vector3dc targetPos, ItemStack stack) {
        world.spawnEntity(new ItemEntity(world, spellPos.x, spellPos.y, spellPos.z, stack));
    }

    protected float cost(double dist) {
        return (float) (20 + Math.pow(dist, (dist / 5)));
    }
}
