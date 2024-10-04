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
    private final Map<Item, Float> itemValues = new IdentityHashMap<>();

    public ConsumeTrick() {
        super(Pattern.of(0, 4, 5, 8, 7, 6, 3, 4, 2));

        itemValues.put(Items.COAL, 6f);
        itemValues.put(Items.AMETHYST_SHARD, 1f);

        // Dynamic entries (careful that depended-on entries aren't removed without also removing relevant dynamic entries!)
        // We can make this system better later, if it gets too big...
        // -- Aurora Dawn
        itemValues.put(Items.COAL_BLOCK, itemValues.get(Items.COAL) * 9);
        itemValues.put(Items.AMETHYST_BLOCK, itemValues.get(Items.AMETHYST_SHARD) * 4);
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        float targetMana = (float) expectInput(fragments, FragmentType.NUMBER, 0).number();
        var slots = expectVariadic(fragments, 1, SlotFragment.class);
        float mana = 0;

        if (!slots.isEmpty()) {
            for (var slot : slots) {
                mana += manaFromSlot(ctx, slot, targetMana - mana);
            }
        } else {
            Optional<ItemStack> stack;

            while (mana < targetMana && (stack = ctx.getStack(this, Optional.empty(), itemValues::containsKey)).isPresent()) {
                mana += itemValues.get(stack.get().getItem());
            }
        }

        var leftover = ctx.source().getManaPool().refill(mana);
        return new NumberFragment(mana - leftover);
    }

    private float manaFromSlot(SpellContext ctx, SlotFragment slot, float targetAmount) throws BlunderException {
        var stack = slot.reference(this, ctx);
        float manaPerItem = Optional.ofNullable(itemValues.get(stack.getItem())).orElse(0f);
        return manaPerItem == 0 ? 0 : slot.move(this, ctx, Math.min(stack.getCount(), Math.round(targetAmount / manaPerItem))).getCount() * manaPerItem;
    }
}
