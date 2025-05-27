package dev.enjarai.trickster.spell.fragment;

import com.google.common.collect.ImmutableList;
import dev.enjarai.trickster.spell.Fragment;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.text.Text;

public record CharFragment(char value, int color) implements Fragment {
    public static final StructEndec<CharFragment> ENDEC = StructEndecBuilder.of(
            Endec.STRING.xmap(s -> s.charAt(0), String::valueOf).fieldOf("value", CharFragment::value),
            Endec.INT.fieldOf("color", CharFragment::color),
            CharFragment::new
    );

    public static CharFragment of(char value) {
        return new CharFragment(value, 0xaabb77);
    }

    public static ListFragment ofString(String value) {
        var builder = ImmutableList.<Fragment>builder();

        for (var c : value.toCharArray()) {
            builder.add(CharFragment.of(c));
        }

        return new ListFragment(builder.build());
    }

    @Override
    public FragmentType<?> type() {
        return FragmentType.CHAR;
    }

    @Override
    public Text asText() {
        return Text.literal(String.valueOf(value)).withColor(color);
    }

    @Override
    public int getWeight() {
        return 4;
    }
}
