package dev.enjarai.trickster.spell.fragment;

import dev.enjarai.trickster.spell.Fragment;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.text.Text;

public record StringFragment(String value) implements Fragment {
    public static final StructEndec<StringFragment> ENDEC = StructEndecBuilder.of(
            StructEndec.STRING.fieldOf("value", StringFragment::value),
            StringFragment::new
    );

    @Override
    public FragmentType<?> type() {
        return FragmentType.STRING;
    }

    @Override
    public Text asText() {
        return Text.literal("\"").append(value).append("\"");
    }

    @Override
    public BooleanFragment asBoolean() {
        return new BooleanFragment(!value.isEmpty());
    }
}
