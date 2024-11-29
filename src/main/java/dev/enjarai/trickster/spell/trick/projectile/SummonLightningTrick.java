package dev.enjarai.trickster.spell.trick.projectile;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.joml.Vector3dc;

import java.util.List;

public class SummonLightningTrick extends AbstractProjectileTrick {
    public SummonLightningTrick() {
        super(Pattern.of(4, 3, 0, 2, 5, 4, 8, 7, 6, 4, 7));
    }

    @Override
    protected Entity makeProjectile(SpellContext ctx, Vector3dc pos, ItemStack stack, List<Fragment> extraInputs) throws BlunderException {
        var lightning = EntityType.LIGHTNING_BOLT.create(ctx.source().getWorld()); assert lightning != null;
        lightning.setPos(pos.x(), pos.y(), pos.z());
        return lightning;
    }

    @Override
    protected boolean isValidItem(Item item) {
        return item.equals(Items.COPPER_INGOT) || item.equals(Items.REDSTONE);
    }
}
