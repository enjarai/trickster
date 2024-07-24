package dev.enjarai.trickster.spell.tricks.inventory;

import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.IndexOutOfBoundsBlunder;
import dev.enjarai.trickster.spell.tricks.blunder.NoPlayerBlunder;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ImportHatTrick extends Trick {
    public ImportHatTrick() {
        super(Pattern.of(3, 0, 5, 6, 3, 2, 5, 8, 3, 1, 5, 7, 3));
    }

    @Override
    public Fragment activate(SpellSource ctx, List<Fragment> fragments) throws BlunderException {
        var index = expectInput(fragments, FragmentType.NUMBER, 0);

        var player = ctx.getPlayer().orElseThrow(() -> new NoPlayerBlunder(this));
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
            return VoidFragment.INSTANCE;
        }

        var container = hatStack.get(DataComponentTypes.CONTAINER);
        if (container != null) {
            if (index.number() < 0 || index.number() >= 27) {
                throw new IndexOutOfBoundsBlunder(this, (int) index.number());
            }

            var scroll = container.stream().skip((long) index.number()).findFirst();
            if (scroll.isEmpty()) {
                return VoidFragment.INSTANCE;
            }

            var component = scroll.get().get(ModComponents.SPELL);
            if (component == null) {
                return VoidFragment.INSTANCE;
            }

            ctx.pushPartGlyph(fragments.subList(1, fragments.size()));
            ctx.pushStackTrace(-2);
            var result = component.spell().run(ctx);
            ctx.popStackTrace();
            ctx.popPartGlyph();
            return result;
        }

        return VoidFragment.INSTANCE;
    }
}
