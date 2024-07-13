package dev.enjarai.trickster.spell.tricks.inventory;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.*;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Optional;

public class GetInventorySlotTrick extends Trick {
    public GetInventorySlotTrick() {
        super(Pattern.of(4, 2, 1, 0, 4, 7, 6, 3, 4, 5, 8, 7, 2, 0, 7));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var slot = expectInput(fragments, FragmentType.NUMBER, 0).number();
        var pos = supposeInput(fragments, FragmentType.VECTOR, 1);
        Optional<BlockPos> maybePosition = Optional.empty();

        if (pos.isPresent()) {
            var target = ctx.getWorld().getBlockEntity(pos.get().toBlockPos());

            if (target instanceof Inventory) {
                maybePosition = Optional.of(target.getPos());
            } else throw new BlockInvalidBlunder(this);
        }

        return new SlotFragment((int)Math.round(slot), maybePosition);
    }
}