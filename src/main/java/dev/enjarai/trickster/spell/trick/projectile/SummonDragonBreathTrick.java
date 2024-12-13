package dev.enjarai.trickster.spell.trick.projectile;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.MissingItemBlunder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
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
    protected Entity makeProjectile(SpellContext ctx, Vector3dc pos, ItemStack stack, List<Fragment> extraInputs) throws BlunderException {
        var optionalSlot2 = supposeInput(extraInputs, FragmentType.SLOT, 0);
        var stack2 = ctx.getStack(this, optionalSlot2, item -> isValidItem(item) && !item.equals(stack.getItem()))
                .orElseThrow(() -> new MissingItemBlunder(this));
        var world = ctx.source().getWorld();

        try {
            var fireball = EntityType.DRAGON_FIREBALL.create(world);
            assert fireball != null;
            fireball.setPos(pos.x(), pos.y(), pos.z());
            return fireball;
        } catch (BlunderException blunder) {
            onFail(ctx, world, ctx.source().getPos(), pos, stack2);
            throw blunder;
        }
    }

    @Override
    protected boolean isValidItem(Item item) {
        return item.equals(Items.DRAGON_BREATH) || item.equals(Items.FIRE_CHARGE);
    }
}
