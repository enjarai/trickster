package dev.enjarai.trickster.spell;

import dev.enjarai.trickster.advancement.criterion.ModCriteria;
import dev.enjarai.trickster.spell.execution.ExecutionState;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Optional;
import java.util.function.Function;

public record SpellContext(SpellSource source, ExecutionState executionState) {
    public void addManaLink(Trick trickSource, LivingEntity target, float limit) throws BlunderException {
        executionState.addManaLink(trickSource, target, source.getHealth(), limit);
    }

    public void useMana(Trick trickSource, float amount) throws BlunderException {
        try {
            executionState.useMana(trickSource, this, executionState.tryOverridePool(source.getManaPool()), amount);
        } catch (NotEnoughManaBlunder blunder) {
            source.getPlayer().ifPresent(ModCriteria.MANA_OVERFLUX::trigger);
            throw blunder;
        }

        source.getPlayer().ifPresent((player) -> ModCriteria.MANA_USED.trigger(player, amount));
    }

    // I am disappointed in myself for having written this.
    // Maybe I'll clean it up one day. -- Aurora D.
    public ItemStack getStack(Trick trickSource, Optional<SlotFragment> optionalSlot, Function<Item, Boolean> validator) throws BlunderException {
        ItemStack result = null;

        if (optionalSlot.isPresent()) {
            if (!validator.apply(optionalSlot.get().getItem(trickSource, this))) throw new ItemInvalidBlunder(trickSource);
            result = optionalSlot.get().move(trickSource, this);
        } else {
            var player = source.getPlayer().orElseThrow(() -> new NoPlayerBlunder(trickSource));
            var inventory = player.getInventory();

            for (int i = 0; i < inventory.size(); i++) {
                var stack = inventory.getStack(i);

                if (validator.apply(stack.getItem())) {
                    result = stack.copyWithCount(1);
                    stack.decrement(1);
                    break;
                }
            }
        }

        if (result == null)
            throw new MissingItemBlunder(trickSource);

        return result;
    }
}
