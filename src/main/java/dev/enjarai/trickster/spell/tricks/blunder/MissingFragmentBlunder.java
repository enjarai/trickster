package dev.enjarai.trickster.spell.tricks.blunder;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.tricks.Trick;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class MissingFragmentBlunder extends TrickBlunderException {
    public final int index;
    public final Text expectedType;

    public MissingFragmentBlunder(Trick source, int index, Text expectedType) {
        super(source);
        this.index = index;
        this.expectedType = expectedType;
    }

    @Override
    public MutableText createMessage() {
        return source.getName().append(": ").append(
                Text.translatable(Trickster.MOD_ID + ".blunder.missing_fragment",
                        formatInt(index), expectedType));
    }
}
