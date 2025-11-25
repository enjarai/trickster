package dev.enjarai.trickster.spell;

import dev.enjarai.trickster.advancement.criterion.ModCriteria;
import dev.enjarai.trickster.spell.blunder.*;
import dev.enjarai.trickster.spell.execution.ExecutionState;
import dev.enjarai.trickster.spell.execution.TickData;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.fragment.slot.SlotFragment;
import dev.enjarai.trickster.spell.fragment.slot.StorageSource;
import dev.enjarai.trickster.spell.fragment.slot.VariantType;
import dev.enjarai.trickster.spell.mana.MutableManaPool;
import dev.enjarai.trickster.spell.trick.Trick;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Optional;
import java.util.function.Predicate;

public record SpellContext(ExecutionState state, SpellSource source, TickData data) {
    public void useMana(Trick<?> trickSource, float amount) throws BlunderException {
        if (Float.isNaN(amount)) {
            throw new IllegalStateException("Internal error: Mana used is NaN");
        }

        if (!data.canUseMana()) {
            throw new NotEnoughManaBlunder(trickSource, amount);
        }

        try {
            state.useMana(trickSource, this, amount);
        } catch (NotEnoughManaBlunder blunder) {
            source.getPlayer().ifPresent(ModCriteria.MANA_OVERFLUX::trigger);
            throw blunder;
        }

        source.getPlayer().ifPresent((player) -> ModCriteria.MANA_USED.trigger(player, amount));
    }

    public void checkMana(Trick<?> trickSource, float amount) throws BlunderException {
        if (Float.isNaN(amount)) {
            throw new IllegalStateException("Internal error: Mana used is NaN");
        }

        if (source.getManaPool().get(source.getWorld()) < amount) {
            source.getPlayer().ifPresent(ModCriteria.MANA_OVERFLUX::trigger);
            throw new NotEnoughManaBlunder(trickSource, amount);
        }
    }

    public MutableManaPool getManaPool() {
        return state().tryOverridePool(source.getManaPool());
    }

    // I am disappointed in myself for having written this.
    // Maybe I'll clean it up one day. -- Aurora D.
    public Optional<ItemStack> getStack(Trick<?> trick, Optional<SlotFragment> optionalSlot, Predicate<ItemStack> validator) throws BlunderException {
        ItemStack result = null;

        if (optionalSlot.isPresent()) {
            var slot = optionalSlot.get().slot().getSelfSlot(trick, this, VariantType.ITEM);

            if (!validator.test(slot.getResource().toStack())) {
                throw new ItemInvalidBlunder(trick);
            }

            try (var trans = Transaction.openOuter()) {
                if (slot.extract(slot.getResource(), 1, trans) == 1) {
                    result = slot.getResource().toStack();
                    trans.commit();
                }
            }
        } else {
            var player = source.getPlayer().orElseThrow(() -> new NoPlayerBlunder(trick));
            var inventory = player.getInventory();

            for (int i = 0; i < inventory.size(); i++) {
                var stack = inventory.getStack(i);

                if (validator.test(stack)) {
                    result = stack.copyWithCount(1);
                    stack.decrement(1);
                    break;
                }
            }
        }

        return Optional.ofNullable(result);
    }

    public SlotFragment findSlotOnPlayer(Trick<?> trick, Item type) {
        var player = source().getPlayer().orElseThrow(() -> new NoPlayerBlunder(trick));
        var inventory = player.getInventory();

        for (int i = 0; i < inventory.size(); i++) {
            var stack = inventory.getStack(i);

            if (stack.isOf(type)) {
                return new SlotFragment(new StorageSource.Slot(i, StorageSource.Caster.INSTANCE), VariantType.ITEM);
            }
        }

        throw new MissingItemBlunder(trick);
    }
}
