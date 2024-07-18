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
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;

import java.util.List;

public class SummonArrowTrick extends Trick {
    public SummonArrowTrick() {
        super(Pattern.of(2, 5, 1, 2, 4, 3, 6, 4, 7, 6));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var pos = expectInput(fragments, FragmentType.VECTOR, 0).vector();
        var optionalSlot = supposeInput(fragments, FragmentType.SLOT, 1);
        ItemStack stack = null;
        boolean b = false;

        if (optionalSlot.isPresent()) {
            stack = optionalSlot.get().getStack(this, ctx);
            b = isValid(stack);
            if (!b) throw new ItemInvalidBlunder(this);
            optionalSlot.get().move(this, ctx);
        } else {
            var player = ctx.getPlayer().orElseThrow(() -> new NoPlayerBlunder(this));
            var inventory = player.getInventory();

            for (int i = 0; i < inventory.size(); i++) {
                stack = inventory.getStack(i);
                b = isValid(stack);
                if (b) break;
            }
        }

        if (stack == null || !b)
            throw new MissingItemBlunder(this);

        ProjectileEntity arrow;

        if (stack.getItem() instanceof ProjectileItem item) {
            var dist = ctx.getPos().distance(pos);
            ctx.useMana(this, (float) (20 + Math.max((dist - 5) * 1.5, 0)));
            stack.decrement(1);
            arrow = item.createEntity(ctx.getWorld(), new Position() {
                @Override
                public double getX() {
                    return pos.x();
                }

                @Override
                public double getY() {
                    return pos.y();
                }

                @Override
                public double getZ() {
                    return pos.z();
                }
            }, stack, Direction.DOWN);
        } else throw new MissingItemBlunder(this);

        ctx.getWorld().spawnEntity(arrow);
        return EntityFragment.from(arrow);
    }

    private static boolean isValid(ItemStack stack) {
        return stack.isIn(ItemTags.ARROWS) && stack.getItem() instanceof ProjectileItem;
    }
}
