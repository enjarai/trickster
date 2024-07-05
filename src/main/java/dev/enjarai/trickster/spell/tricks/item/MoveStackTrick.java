package dev.enjarai.trickster.spell.tricks.item;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.*;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

public class MoveStackTrick extends Trick {
    public MoveStackTrick() {
        super(Pattern.of(4, 1, 0, 3, 4, 8, 7, 5, 8));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var stack = expectInput(fragments, FragmentType.ITEM_STACK, 0).stack();
        var target = expectInput(fragments, FragmentType.ENTITY, 1).getEntity(ctx);

        if (stack.isEmpty())
            return VoidFragment.INSTANCE;

        if (target.isPresent()) {
            var entity2 = target.get();

            if (entity2 instanceof PlayerEntity player) {
                var inventory = player.getInventory();
                int occupiedSlot;
                int emptySlot;

                while ((occupiedSlot = inventory.getOccupiedSlotWithRoomForStack(stack)) != -1) {
                    var occupied = inventory.getStack(occupiedSlot);
                    int canAdd = occupied.getMaxCount() - occupied.getCount();
                    canAdd = Math.min(canAdd, stack.getCount());
                    occupied.increment(canAdd);
                    stack.decrement(canAdd);
                }

                if ((emptySlot = inventory.getEmptySlot()) != -1) {
                    inventory.setStack(emptySlot, stack.copyAndEmpty());
                } else {
                    throw new NoFreeSlotBlunder(this);
                }

                return VoidFragment.INSTANCE;
            }

            throw new EntityInvalidBlunder(this);
        }

        throw new UnknownEntityBlunder(this);
    }
}
