package dev.enjarai.trickster.spell.tricks.projectile;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.ItemInvalidBlunder;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3dc;

import java.util.List;

public class SummonArrowTrick extends AbstractProjectileTrick {
    public SummonArrowTrick() {
        super(Pattern.of(2, 5, 1, 2, 4, 3, 6, 4, 7, 6));
    }

    @Override
    protected Entity makeProjectile(SpellContext ctx, Vector3dc pos, ItemStack stack, List<Fragment> extraInputs) throws BlunderException {
        if (stack.getItem() instanceof ProjectileItem item) {
            return item.createEntity(ctx.source().getWorld(), new Vec3d(0, 0, 0).fromVector3d(pos), stack, Direction.DOWN);
        } else throw new ItemInvalidBlunder(this);
    }

    @Override
    protected boolean isValidItem(Item item) {
        return item.getRegistryEntry().isIn(ItemTags.ARROWS) && item instanceof ProjectileItem;
    }
}
