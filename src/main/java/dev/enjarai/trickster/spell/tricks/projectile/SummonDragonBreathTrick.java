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
    protected Entity makeProjectile(SpellContext ctx, Vector3dc pos, ItemStack stack, List<Fragment> extraInputs) throws BlunderException {
        var fireball = EntityType.DRAGON_FIREBALL.create(ctx.getWorld()); assert fireball != null;
        fireball.setPos(pos.x(), pos.y(), pos.z());
        return fireball;
    }

    @Override
    protected boolean isValidItem(Item item) {
        return item.equals(Items.DRAGON_BREATH) || item.equals(Items.FIRE_CHARGE);
    }
}
