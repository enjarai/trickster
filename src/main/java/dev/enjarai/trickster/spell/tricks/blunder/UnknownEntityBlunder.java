package dev.enjarai.trickster.spell.tricks.blunder;

import dev.enjarai.trickster.spell.tricks.Trick;
import net.minecraft.text.MutableText;

public class UnknownEntityBlunder extends TrickBlunderException {
    public UnknownEntityBlunder(Trick source) {
        super(source);
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append("Unknown entity");
    }
}
