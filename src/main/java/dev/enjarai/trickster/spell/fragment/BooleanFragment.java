package dev.enjarai.trickster.spell.fragment;

import dev.enjarai.trickster.spell.Fragment;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.text.Text;

public class BooleanFragment implements Fragment {

    final boolean bool;

    private BooleanFragment(boolean bool) {
        this.bool = bool;
    }

    public static final StructEndec<BooleanFragment> ENDEC = StructEndecBuilder.of(
            Endec.BOOLEAN.fieldOf("bool", BooleanFragment::asBoolean),
            BooleanFragment::of
    );

    public static final BooleanFragment TRUE = new BooleanFragment(true);
    public static final BooleanFragment FALSE = new BooleanFragment(false);

    public static BooleanFragment of(boolean bool) { return bool ? TRUE : FALSE; }

    @Override
    public FragmentType<?> type() {
        return FragmentType.BOOLEAN;
    }

    @Override
    public Text asText() {
        return Text.literal("" + bool);
    }

    @Override
    public boolean asBoolean() {
        return bool;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BooleanFragment other && other.bool == this.bool;
    }
}
