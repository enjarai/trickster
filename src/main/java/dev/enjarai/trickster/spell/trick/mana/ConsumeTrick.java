package dev.enjarai.trickster.spell.trick.mana;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ConsumeTrick extends Trick {
    private Map<Item, Float> itemValues = new IdentityHashMap<>();

    public ConsumeTrick() {
        super(Pattern.of(/*TODO*/));

        itemValues.put(Items.COAL, (float) 10);
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        float targetMana = (float) expectInput(fragments, FragmentType.NUMBER, 0).number();
        var slots = expectVariadic(fragments, 1, SlotFragment.class);
        float mana = 0;

        if (slots.size() > 0) {
            for (var slot : slots) {
                mana += manaFromSlot(ctx, slot, targetMana - mana);
            }
        } else {
            Optional<ItemStack> stack;

            while (mana < targetMana && (stack = ctx.getStack(this, Optional.empty(), item -> itemValues.keySet().contains(item))).isPresent()) {
                mana += Optional.ofNullable(itemValues.get(stack.get().getItem()))
                    // This orElseThrow should never happen, but if it does, we don't want it hanging...
                    .orElseThrow(() -> new IllegalStateException("Item to mana map failed to retrieve value from key, despite item supposedly being a valid key"));
            }
        }

        ctx.source().getManaPool().refill(mana); //TODO: give mana boost a use by having a rebate for excess mana?
        return new NumberFragment(mana);
    }

    private float manaFromSlot(SpellContext ctx, SlotFragment slot, float targetAmount) throws BlunderException {
        var stack = slot.reference(this, ctx);
        float manaPerItem = Optional.ofNullable(itemValues.get(stack.getItem())).orElse((float) 0);
        return manaPerItem == 0 ? 0 : slot.move(this, ctx, Math.min(stack.getCount(), Math.round(targetAmount / manaPerItem))).getCount() * manaPerItem;
    }
}
