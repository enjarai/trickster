package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;

public class NumberIsInfiniteBlunder extends TrickBlunderException {
    public NumberIsInfiniteBlunder(Trick<?> source) {
        super(source);
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append("Number cannot be infinite");
    }
}
