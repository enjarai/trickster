package dev.enjarai.trickster.spell.tricks.projectile;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.ItemInvalidBlunder;
import dev.enjarai.trickster.spell.tricks.blunder.MissingItemBlunder;
import dev.enjarai.trickster.spell.tricks.blunder.NoPlayerBlunder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
        var stack = ctx.getStack(this, optionalSlot, this::isValidItem);
        var world = ctx.getWorld();

        try {
            var projectile = makeProjectile(ctx, pos, stack, fragments.subList(optionalSlot.isPresent() ? 2 : 1, fragments.size()));
            world.spawnEntity(projectile);
            return EntityFragment.from(projectile);
        } catch (BlunderException blunder) {
            var thisPos = ctx.getPos();
            world.spawnEntity(new ItemEntity(world, thisPos.x, thisPos.y, thisPos.z, stack));
            throw blunder;
        }
    }

    protected abstract Entity makeProjectile(SpellContext ctx, Vector3dc pos, ItemStack stack, List<Fragment> extraInputs) throws BlunderException;

    protected abstract boolean isValidItem(Item item);
}
