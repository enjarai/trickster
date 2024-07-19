package dev.enjarai.trickster.spell.tricks.projectile;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.MissingItemBlunder;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import org.joml.Vector3dc;

import java.util.List;

public class SummonArrowTrick extends AbstractProjectileTrick {
    public SummonArrowTrick() {
        super(Pattern.of(2, 5, 1, 2, 4, 3, 6, 4, 7, 6));
    }

    @Override
    protected Entity makeProjectile(SpellContext ctx, Vector3dc pos, ItemStack stack, List<Fragment> extraInputs) throws BlunderException {
        if (stack.getItem() instanceof ProjectileItem item) {
            var dist = ctx.getPos().distance(pos);
            ctx.useMana(this, (float) (20 + Math.max((dist - 5) * 1.5, 0)));
            return item.createEntity(ctx.getWorld(), new Position() {
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
    }

    @Override
    protected boolean isValidItem(Item item) {
        return item.getRegistryEntry().isIn(ItemTags.ARROWS) && item instanceof ProjectileItem;
    }
}
