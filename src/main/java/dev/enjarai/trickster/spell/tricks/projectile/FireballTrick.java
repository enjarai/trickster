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
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ProjectileItem;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;

import java.util.List;

public class FireballTrick extends Trick {
    public FireballTrick() {
        super(Pattern.of(0, 1, 2, 3, 4));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var pos = expectInput(fragments, FragmentType.VECTOR, 0).vector();
        ItemStack stack = null;
        boolean b = false;

        var player = ctx.getPlayer().orElseThrow(() -> new NoPlayerBlunder(this));
        var inventory = player.getInventory();

        for (int i = 0; i < inventory.size(); i++) {
            stack = inventory.getStack(i);
            b = stack.getItem().equals(Items.FIRE_CHARGE);
            if (b) break;
        }

        if (stack == null || !b)
            throw new MissingItemBlunder(this);

        var dist = ctx.getPos().distance(pos);
        ctx.useMana(this, (float)(20 + (dist > 5 ? dist * 1.5 : 0)));
        stack.decrement(1);

        var fireball = EntityType.FIREBALL.create(ctx.getWorld()); assert fireball != null;
        fireball.setPos(pos.x(), pos.y(), pos.z());

        ctx.getWorld().spawnEntity(fireball);
        return EntityFragment.from(fireball);
    }
}
