package dev.enjarai.trickster.spell.tricks.inventory;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ItemStackFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.*;
import net.minecraft.inventory.Inventory;

import java.util.List;

public class GetInventoryStackTrick extends Trick {
    public GetInventoryStackTrick() {
        super(Pattern.of(4, 2, 1, 0, 4, 7, 6, 3, 4, 5, 8, 7, 2, 0, 7));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var slot = expectInput(fragments, FragmentType.NUMBER, 1).number();
        var pos = supposeInput(fragments, FragmentType.VECTOR, 0);
        Inventory inventory;

        if (pos.isPresent()) {
            var target = ctx.getWorld().getBlockEntity(pos.get().toBlockPos());

            if (target instanceof Inventory blockInventory) {
                inventory = blockInventory;
            } else throw new BlockInvalidBlunder(this);
        } else {
            var player = ctx.getPlayer();

            if (player.isPresent())
                inventory = player.get().getInventory();
            else throw new NoPlayerBlunder(this);
        }

        if (slot > inventory.size())
            throw new NoSuchSlotBlunder(this);

        return new ItemStackFragment(inventory.getStack((int)Math.round(slot)));
    }
}