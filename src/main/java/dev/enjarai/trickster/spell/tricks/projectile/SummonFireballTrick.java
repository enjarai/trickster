package dev.enjarai.trickster.spell.tricks.projectile;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.joml.Vector3dc;

import java.util.List;

public class SummonFireballTrick extends AbstractProjectileTrick {
    public SummonFireballTrick() {
        super(Pattern.of(1, 4, 0, 3, 6, 7, 8, 5, 2, 1, 5, 4, 8, 6, 4, 3));
    }

    @Override
    protected Entity makeProjectile(SpellContext ctx, Vector3dc pos, ItemStack stack, List<Fragment> extraInputs) throws BlunderException {
        var dist = ctx.getPos().distance(pos);
        ctx.useMana(this, (float) (20 + Math.max((dist - 5) * 1.5, 0)));
        stack.decrement(1);

        var fireball = EntityType.FIREBALL.create(ctx.getWorld()); assert fireball != null;
        fireball.setPos(pos.x(), pos.y(), pos.z());

        return fireball;
    }

    @Override
    protected boolean isValidItem(ItemStack stack) {
        return stack.getItem().equals(Items.FIRE_CHARGE);
    }
}
