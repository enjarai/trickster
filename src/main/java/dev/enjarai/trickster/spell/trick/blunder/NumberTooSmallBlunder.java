package dev.enjarai.trickster.spell.trick.blunder;

import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;

public class NumberTooSmallBlunder extends TrickBlunderException {
    private final int minimum;

    public NumberTooSmallBlunder(Trick source, int minimum) {
        super(source);
        this.minimum = minimum;
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append("Number too small, expected ").append("%d".formatted(minimum)).append(" or greater");
    }
}
