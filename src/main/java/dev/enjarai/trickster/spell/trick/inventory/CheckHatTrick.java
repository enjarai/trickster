package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.NoPlayerBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

import java.util.Optional;

public class CheckHatTrick extends Trick<CheckHatTrick> {
    public CheckHatTrick() {
        super(Pattern.of(3, 0, 2, 5, 8, 6, 3, 1, 5, 7, 3), Signature.of(CheckHatTrick::run, FragmentType.NUMBER.maybe()));
    }

    public Optional<NumberFragment> run(SpellContext ctx) throws BlunderException {
        var player = ctx.source().getPlayer().orElseThrow(() -> new NoPlayerBlunder(this));
        ItemStack hatStack;

        var hatSlot = SlotReference.of(player, "hat", 0);
        var hatSlotStack = hatSlot.getStack();
        if (hatSlotStack != null && hatSlotStack.isIn(ModItems.HOLDABLE_HAT)) {
            hatStack = hatSlotStack;
        } else if (player.getOffHandStack().isIn(ModItems.HOLDABLE_HAT)) {
            hatStack = player.getOffHandStack();
        } else if (player.getEquippedStack(EquipmentSlot.HEAD).isIn(ModItems.HOLDABLE_HAT)) {
            hatStack = player.getEquippedStack(EquipmentSlot.HEAD);
        } else {
            return Optional.empty();
        }

        var slot = hatStack.get(ModComponents.SELECTED_SLOT);
        if (slot != null) {
            return Optional.of(new NumberFragment(slot.slot()));
        }

        return Optional.empty();
    }
}
