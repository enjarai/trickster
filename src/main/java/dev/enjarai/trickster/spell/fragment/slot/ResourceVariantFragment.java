package dev.enjarai.trickster.spell.fragment.slot;

import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.trick.Trick;

public interface ResourceVariantFragment<T> {
    VariantType<T> variantType();

    boolean slotContains(Trick<?> trick, SpellContext ctx, SlotFragment slotFragment);
}
