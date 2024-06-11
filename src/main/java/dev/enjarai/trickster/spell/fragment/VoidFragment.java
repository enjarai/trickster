package dev.enjarai.trickster.spell.fragment;

import com.mojang.serialization.MapCodec;
import dev.enjarai.trickster.spell.Fragment;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public record VoidFragment() implements Fragment {
    public static final VoidFragment INSTANCE = new VoidFragment();
    public static final MapCodec<VoidFragment> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public FragmentType<?> type() {
        return FragmentType.VOID;
    }

    @Override
    public Text asText() {
        return Text.literal("void").fillStyle(Style.EMPTY.withColor(0x4400aa));
    }

    @Override
    public BooleanFragment asBoolean() {
        return BooleanFragment.FALSE;
    }
}
