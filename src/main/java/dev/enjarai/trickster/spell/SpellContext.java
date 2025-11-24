package dev.enjarai.trickster.spell;

import dev.enjarai.trickster.advancement.criterion.ModCriteria;
import dev.enjarai.trickster.spell.blunder.*;
import dev.enjarai.trickster.spell.execution.ExecutionState;
import dev.enjarai.trickster.spell.execution.TickData;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.fragment.slot.SlotFragment;
import dev.enjarai.trickster.spell.mana.MutableManaPool;
import dev.enjarai.trickster.spell.trick.Trick;
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
    public Optional<ItemStack> getStack(Trick<?> trickSource, Optional<SlotFragment> optionalSlot, Predicate<ItemStack> validator) throws BlunderException {
        ItemStack result = null;

        if (optionalSlot.isPresent()) {
            if (!validator.test(optionalSlot.get().reference(trickSource, this))) {
                throw new ItemInvalidBlunder(trickSource);
            }

            result = optionalSlot.get().move(trickSource, this, 1);
        } else {
            var player = source.getPlayer().orElseThrow(() -> new NoPlayerBlunder(trickSource));
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
}
