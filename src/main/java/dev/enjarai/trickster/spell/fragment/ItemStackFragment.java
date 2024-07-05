package dev.enjarai.trickster.spell.fragment;

import com.mojang.serialization.MapCodec;
import dev.enjarai.trickster.spell.Fragment;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public record ItemStackFragment(ItemStack stack) implements Fragment {
    public static final MapCodec<ItemStackFragment> CODEC = ItemStack.CODEC
        .xmap(ItemStackFragment::new, ItemStackFragment::stack).fieldOf("stack");

    @Override
    public FragmentType<?> type() {
        return FragmentType.ITEM_STACK;
    }

    @Override
    public Text asText() {
        return Text.literal("stack of %s with %d items".formatted(stack.getName(), stack.getCount())); //TODO
    }

    @Override
    public BooleanFragment asBoolean() {
        return BooleanFragment.TRUE;
    }
}
