package dev.enjarai.trickster.spell.fragment;

import com.google.common.collect.ImmutableList;
import dev.enjarai.trickster.spell.Fragment;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.text.Text;

import java.util.List;

public record StringFragment(List<CharFragment> chars) implements Fragment {
    public static final StructEndec<StringFragment> ENDEC = StructEndecBuilder.of(
            CharFragment.ENDEC.listOf().fieldOf("chars", StringFragment::chars),
            StringFragment::new
    );

    public static StringFragment of(Text value) {
        var builder = ImmutableList.<CharFragment>builder();
        extractFromText(builder, value);
        return new StringFragment(builder.build());
    }

    public static void extractFromText(ImmutableList.Builder<CharFragment> builder, Text value) {
        value.asOrderedText().accept((index, style, codePoint) -> {
            var color = 0xffffff;
            if (style.getColor() != null) {
                color = style.getColor().getRgb();
            }
            // Im sure just grabbing the first char wont have any consequences whatshowever
            // Of course 16 bits is enough to represent every possible unicode character
            builder.add(new CharFragment(Character.toChars(codePoint)[0], color));
            return true;
        });
    }

    @Override
    public FragmentType<?> type() {
        return FragmentType.STRING;
    }

    @Override
    public Text asText() {
        var result = Text.literal("");

        for (var charFragment : chars) {
            result = result.append(charFragment.asFormattedText());
        }

        return result;
    }

    @Override
    public int getWeight() {
        return 4;
    }
}
