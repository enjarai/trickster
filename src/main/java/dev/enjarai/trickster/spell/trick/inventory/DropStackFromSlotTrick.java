package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.NumberTooSmallBlunder;
import net.minecraft.entity.ItemEntity;

import java.util.Optional;

public class DropStackFromSlotTrick extends Trick<DropStackFromSlotTrick> {
    public DropStackFromSlotTrick() {
        super(Pattern.of(1, 4, 7, 3, 4, 5, 7), Signature.of(FragmentType.SLOT, FragmentType.VECTOR, FragmentType.NUMBER.optionalOf(), DropStackFromSlotTrick::run));
    }

    public Fragment run(SpellContext ctx, SlotFragment slot, VectorFragment pos, Optional<NumberFragment> optionalAmount) throws BlunderException {
        var vector = pos.vector();
        var amount = optionalAmount.orElse(new NumberFragment(1)).number();

        if (amount < 1) {
            throw new NumberTooSmallBlunder(this, 1);
        }

        var stack = slot.move(this, ctx, (int) Math.round(amount), pos.toBlockPos());
        var world = ctx.source().getWorld();
        var entity = new ItemEntity(world, vector.x(), vector.y(), vector.z(), stack);
        entity.setPickupDelay(10);

        world.spawnEntity(entity);
        return new EntityFragment(entity.getUuid(), entity.getName());
    }
}
