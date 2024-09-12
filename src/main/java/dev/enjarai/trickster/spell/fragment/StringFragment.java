package dev.enjarai.trickster.spell.fragment;

import dev.enjarai.trickster.spell.Fragment;
import io.wispforest.accessories.endec.MinecraftEndecs;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.text.Text;

public record StringFragment(Text value) implements Fragment {
    public static final StructEndec<StringFragment> ENDEC = StructEndecBuilder.of(
            MinecraftEndecs.TEXT.fieldOf("value", StringFragment::value),
            StringFragment::new
    );

    @Override
    public FragmentType<?> type() {
        return FragmentType.STRING;
    }

    @Override
    public Text asText() {
        return value;
    }

    @Override
    public Text asFormattedText() {
        if (type().color().isPresent()) {
            return Text.literal("\"")
                    .append(asText())
                    .append(Text.literal("\""))
                    .withColor(type().color().getAsInt());
        }

        return asText();
    }

    @Override
    public BooleanFragment asBoolean() {
        return new BooleanFragment(!value.getString().isEmpty());
    }
}
