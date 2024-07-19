package dev.enjarai.trickster.spell.tricks.projectile;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.joml.Vector3dc;

import java.util.List;

public class SummonDragonBreathTrick extends AbstractProjectileTrick {
    public SummonDragonBreathTrick() {
        super(Pattern.of(1, 0, 3, 6, 7, 8, 5, 2, 1, 6, 8, 1, 4, 7, 0, 2, 7));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var pos = expectInput(fragments, FragmentType.VECTOR, 0).vector();
        var optionalSlot1 = supposeInput(fragments, FragmentType.SLOT, 1);
        var optionalSlot2 = supposeInput(fragments, FragmentType.SLOT, 2);
        var stack1 = ctx.getStack(this, optionalSlot1, this::isValidItem);
        var stack2 = ctx.getStack(this, optionalSlot2, item -> isValidItem(item) && !item.equals(stack1.getItem()));
        var world = ctx.getWorld();

        try {
            var projectile = makeProjectile(ctx, pos, stack1,
                    fragments.subList(1 + (optionalSlot1.isPresent() ? 1 : 0) + (optionalSlot2.isPresent() ? 1 : 0), fragments.size()));
            world.spawnEntity(projectile);
            return EntityFragment.from(projectile);
        } catch (BlunderException blunder) {
            var thisPos = ctx.getPos();
            world.spawnEntity(new ItemEntity(world, thisPos.x, thisPos.y, thisPos.z, stack1));
            world.spawnEntity(new ItemEntity(world, thisPos.x, thisPos.y, thisPos.z, stack2));
            throw blunder;
        }
    }

    @Override
    protected Entity makeProjectile(SpellContext ctx, Vector3dc pos, ItemStack stack, List<Fragment> extraInputs) throws BlunderException {
        var dist = ctx.getPos().distance(pos);
        ctx.useMana(this, (float) (20 + Math.max((dist - 5) * 1.5, 0)));
        var fireball = EntityType.DRAGON_FIREBALL.create(ctx.getWorld()); assert fireball != null;
        fireball.setPos(pos.x(), pos.y(), pos.z());
        return fireball;
    }

    @Override
    protected boolean isValidItem(Item item) {
        return item.equals(Items.DRAGON_BREATH) || item.equals(Items.FIRE_CHARGE);
    }
}
