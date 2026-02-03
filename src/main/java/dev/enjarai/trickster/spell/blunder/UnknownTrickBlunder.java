package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.compat.ModCompat;
import dev.enjarai.trickster.compat.inline.PatternData;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class UnknownTrickBlunder extends BlunderException {
    public final Pattern pattern;

    public UnknownTrickBlunder(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public MutableText createMessage() {
        if (ModCompat.INLINE_LOADED) {
            var message = Text.empty();
            message.append(PatternData.make(pattern, Style.EMPTY.withColor(FragmentType.PATTERN.color().getAsInt())).append(" Unknown"));
            message.append(": ");
            message.append(Text.translatable("trickster.blunder.unknown_trick"));
            return message;
        } else {
            return Text.translatable("trickster.blunder.unknown_trick").withColor(FragmentType.PATTERN.color().getAsInt());
        }
    }
}
