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
        ItemStack stack = null;
        boolean b = false;

        if (optionalSlot.isPresent()) {
            stack = optionalSlot.get().getStack(this, ctx);
            b = isValidItem(stack);
            if (!b) throw new ItemInvalidBlunder(this);
            optionalSlot.get().move(this, ctx);
        } else {
            var player = ctx.getPlayer().orElseThrow(() -> new NoPlayerBlunder(this));
            var inventory = player.getInventory();

            for (int i = 0; i < inventory.size(); i++) {
                stack = inventory.getStack(i);
                b = isValidItem(stack);
                if (b) break;
            }
        }

        if (stack == null || !b)
            throw new MissingItemBlunder(this);

        var projectile = makeProjectile(ctx, pos, stack, fragments.subList(optionalSlot.isPresent() ? 2 : 1, fragments.size()));
        ctx.getWorld().spawnEntity(projectile);
        return EntityFragment.from(projectile);
    }

    protected abstract Entity makeProjectile(SpellContext ctx, Vector3dc pos, ItemStack stack, List<Fragment> extraInputs) throws BlunderException;

    protected abstract boolean isValidItem(ItemStack stack);
}
