package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;

public class NumberTooLargeBlunder extends TrickBlunderException {
    private final int maximum;

    public NumberTooLargeBlunder(Trick<?> source, int maximum) {
        super(source);
        this.maximum = maximum;
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append("Number too large, expected ").append("%d".formatted(maximum)).append(" or lesser");
    }
}
