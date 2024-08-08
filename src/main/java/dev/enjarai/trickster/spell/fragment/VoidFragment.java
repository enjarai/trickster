package dev.enjarai.trickster.spell.fragment;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.spell.Fragment;
import io.wispforest.endec.StructEndec;
import net.minecraft.text.Text;

public record VoidFragment() implements Fragment {
    public static final VoidFragment INSTANCE = new VoidFragment();
    public static final StructEndec<VoidFragment> ENDEC = EndecTomfoolery.unit(INSTANCE);

    @Override
    public FragmentType<?> type() {
        return FragmentType.VOID;
    }

    @Override
    public Text asText() {
        return Text.literal("void");
    }

    @Override
    public BooleanFragment asBoolean() {
        return BooleanFragment.FALSE;
    }
}
