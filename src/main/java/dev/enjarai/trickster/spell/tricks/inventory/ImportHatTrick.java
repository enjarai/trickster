package dev.enjarai.trickster.spell.tricks.inventory;

import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.execution.SpellExecutor;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.*;
import dev.enjarai.trickster.spell.tricks.func.ForkingTrick;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ImportHatTrick extends Trick implements ForkingTrick {
    public ImportHatTrick() {
        super(Pattern.of(3, 0, 5, 6, 3, 2, 5, 8, 3, 1, 5, 7, 3));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return null;
    }

    @Override
    public SpellExecutor makeFork(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var index = expectInput(fragments, FragmentType.NUMBER, 0);

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
            throw new MissingItemBlunder(this);
        }

        var container = hatStack.get(DataComponentTypes.CONTAINER);
        if (container != null) {
            if (index.number() < 0 || index.number() >= 27) {
                throw new IndexOutOfBoundsBlunder(this, (int) index.number());
            }

            var scroll = container.stream().skip((long) index.number()).findFirst();
            if (scroll.isEmpty()) {
                throw new MissingItemBlunder(this);
            }

            var component = scroll.get().get(ModComponents.SPELL);
            if (component == null) {
                throw new ItemInvalidBlunder(this);
            }

            return new SpellExecutor(component.spell(), fragments.subList(1, fragments.size()));
        }

        throw new ItemInvalidBlunder(this);
    }
}
