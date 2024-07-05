package dev.enjarai.trickster.spell.tricks.item;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ItemStackFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.EntityInvalidBlunder;
import dev.enjarai.trickster.spell.tricks.blunder.NoSuchSlotBlunder;
import dev.enjarai.trickster.spell.tricks.blunder.UnknownEntityBlunder;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

public class GetInventoryStackTrick extends Trick {
    public GetInventoryStackTrick() {
        super(Pattern.of(4, 2, 1, 0, 4, 7, 6, 3, 4, 5, 8, 7, 2, 0, 7));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var entity = expectInput(fragments, FragmentType.ENTITY, 0).getEntity(ctx);
        var slot = expectInput(fragments, FragmentType.NUMBER, 1).number();

        if (entity.isPresent()) {
            var entity2 = entity.get();

            if (entity2 instanceof PlayerEntity player) {
                var inventory = player.getInventory();

                if (slot > inventory.size())
                    throw new NoSuchSlotBlunder(this);

                return new ItemStackFragment(inventory.getStack((int)Math.round(slot)));
            }

            throw new EntityInvalidBlunder(this);
        }

        throw new UnknownEntityBlunder(this);
    }
}
