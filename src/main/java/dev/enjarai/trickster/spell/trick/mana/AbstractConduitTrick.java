package dev.enjarai.trickster.spell.trick.mana;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.item.ItemStack;

import java.util.List;

public abstract class AbstractConduitTrick extends Trick<AbstractConduitTrick> {
    public AbstractConduitTrick(Pattern pattern) {
        super(pattern, Signature.of(FragmentType.NUMBER, FragmentType.SLOT.variadicOfArg().require().unpack(), AbstractConduitTrick::run, FragmentType.NUMBER));
    }

    public NumberFragment run(SpellContext ctx, NumberFragment n, List<SlotFragment> slots) throws BlunderException {
        double limit = n.number() / slots.size();
        float result = 0;

        for (var slot : slots) {
            var distance = ctx.source().getPos().distance(slot.getSourceOrCasterPos(this, ctx));
            float r = Trickster.CONFIG.manaTransferEfficiency();
            double tax = Math.max(0, 1 - r / (distance + r - 16));
            result += affect(ctx, slot.reference(this, ctx), (float) limit, tax);
        }

        return new NumberFragment(result);
    }

    /**
     * @return the amount of mana fulfilled by the item.
     */
    protected abstract float affect(SpellContext ctx, ItemStack stack, float limit, double taxPercentage);
}
