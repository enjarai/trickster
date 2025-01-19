package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class NumberTooSmallBlunder extends TrickBlunderException {
    private final int minimum;

    public NumberTooSmallBlunder(Trick<?> source, int minimum) {
        super(source);
        this.minimum = minimum;
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append(Text.translatable(Trickster.MOD_ID + ".blunder.number_too_small", minimum));
    }
}
