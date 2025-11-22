package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.item.component.SelectedSlotComponent;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.NoPlayerBlunder;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class SetHatTrick extends Trick<SetHatTrick> {
    public SetHatTrick() {
        super(Pattern.of(1, 3, 7, 5, 2, 0, 3, 6, 8, 5, 1, 4, 7), Signature.of(FragmentType.NUMBER, SetHatTrick::run, FragmentType.BOOLEAN));
    }

    public BooleanFragment run(SpellContext ctx, NumberFragment number) {
        var newSlot = number.number();

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
            return BooleanFragment.FALSE;
        }

        var slot = hatStack.get(ModComponents.SELECTED_SLOT);
        if (slot != null) {
            hatStack.set(
                    ModComponents.SELECTED_SLOT, new SelectedSlotComponent(
                            ((int) Math.floor(Math.abs(newSlot))) % slot.maxSlot(), slot.maxSlot()
                    )
            );
            return BooleanFragment.TRUE;
        }

        return BooleanFragment.FALSE;
    }
}
