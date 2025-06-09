package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.NumberTooSmallBlunder;
import dev.enjarai.trickster.spell.fragment.*;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.entity.ItemEntity;

import java.util.Optional;

public class DropStackFromSlotTrick extends Trick<DropStackFromSlotTrick> {
    public DropStackFromSlotTrick() {
        super(Pattern.of(1, 4, 7, 3, 4, 5, 7), Signature.of(FragmentType.SLOT, FragmentType.VECTOR, FragmentType.NUMBER.optionalOf(), DropStackFromSlotTrick::run, FragmentType.ENTITY));
    }

    public EntityFragment run(SpellContext ctx, SlotFragment slot, VectorFragment pos, Optional<NumberFragment> optionalAmount) throws BlunderException {
        var vector = pos.vector();
        var amount = optionalAmount.orElse(new NumberFragment(1)).number();

        if (amount < 1) {
            throw new NumberTooSmallBlunder(this, 1);
        }

        var stack = slot.move(this, ctx, (int) Math.round(amount), vector);
        var world = ctx.source().getWorld();
        var entity = new ItemEntity(world, vector.x(), vector.y(), vector.z(), stack);
        entity.setPickupDelay(10);

        world.spawnEntity(entity);
        return new EntityFragment(entity.getUuid(), entity.getName());
    }
}
