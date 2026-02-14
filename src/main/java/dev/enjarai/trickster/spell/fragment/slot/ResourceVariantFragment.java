package dev.enjarai.trickster.spell.fragment.slot;

import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.trick.Trick;

public interface ResourceVariantFragment<T> extends VariantingFragment {
    VariantType<T> variantType();

    default boolean slotContains(Trick<?> trick, SpellContext ctx, SlotFragment slotFragment) {
        return resourceMatches(trick, ctx, slotFragment.getResource(trick, ctx, variantType()));
    }

    boolean resourceMatches(Trick<?> trick, SpellContext ctx, T resource);
}
