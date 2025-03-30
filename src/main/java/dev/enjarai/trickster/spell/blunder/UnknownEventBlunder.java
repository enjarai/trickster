package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;

public class UnknownEventBlunder extends TrickBlunderException {
    public UnknownEventBlunder(Trick<?> source) {
        super(source);
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append("Unknown event");
    }
}
