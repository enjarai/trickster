package dev.enjarai.trickster.spell.fragment;

import dev.enjarai.trickster.spell.Fragment;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.text.Text;

public record BooleanFragment(boolean bool) implements Fragment {
    public static final StructEndec<BooleanFragment> ENDEC = StructEndecBuilder.of(
            Endec.BOOLEAN.fieldOf("bool", BooleanFragment::bool),
            BooleanFragment::new
    );
    public static final BooleanFragment TRUE = new BooleanFragment(true);
    public static final BooleanFragment FALSE = new BooleanFragment(false);

    @Override
    public FragmentType<?> type() {
        return FragmentType.BOOLEAN;
    }

    @Override
    public Text asText() {
        return Text.literal("" + bool);
    }

    @Override
    public BooleanFragment asBoolean() {
        return bool ? TRUE : FALSE;
    }

    @Override
    public int getWeight() {
        return 1;
    }
}
