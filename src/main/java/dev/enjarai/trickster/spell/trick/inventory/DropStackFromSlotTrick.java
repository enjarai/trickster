package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.NumberTooSmallBlunder;
import net.minecraft.entity.ItemEntity;

import java.util.List;

public class DropStackFromSlotTrick extends Trick {
    public DropStackFromSlotTrick() {
        super(Pattern.of(1, 4, 7, 3, 4, 5, 7));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var slot = expectInput(fragments, FragmentType.SLOT, 0);
        var pos = expectInput(fragments, FragmentType.VECTOR, 1).toBlockPos();
        var amount = supposeInput(fragments, FragmentType.NUMBER, 2).orElse(new NumberFragment(1)).number();

        if (amount < 1)
            throw new NumberTooSmallBlunder(this, 1);

        var stack = slot.move(this, ctx, (int) Math.round(amount), pos);
        var world = ctx.source().getWorld();
        var entity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);

        world.spawnEntity(entity);
        return new EntityFragment(entity.getUuid(), entity.getName());
    }
}
