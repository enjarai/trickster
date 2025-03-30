package dev.enjarai.trickster.spell.trick.mana;

import java.util.List;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.item.ItemStack;

public abstract class AbstractConduitTrick extends Trick<AbstractConduitTrick> {
    public AbstractConduitTrick(Pattern pattern) {
        super(pattern, Signature.of(FragmentType.NUMBER, variadic(FragmentType.SLOT).require().unpack(), AbstractConduitTrick::run));
    }

    public Fragment run(SpellContext ctx, NumberFragment n, List<SlotFragment> slots) throws BlunderException {
        double limit = n.number() / slots.size();
        float result = 0;

        for (var slot : slots) {
            result += affect(ctx, slot.reference(this, ctx), (float) limit);
        }

        return new NumberFragment(result);
    }

    /**
     * @return the amount of mana fulfilled by the item.
     */
    protected abstract float affect(SpellContext ctx, ItemStack stack, float limit);
}
