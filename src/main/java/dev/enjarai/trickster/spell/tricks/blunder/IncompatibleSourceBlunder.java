package dev.enjarai.trickster.spell.tricks.blunder;

import dev.enjarai.trickster.spell.tricks.Trick;
import net.minecraft.text.MutableText;

public class IncompatibleSourceBlunder extends TrickBlunderException {
    public IncompatibleSourceBlunder(Trick source) {
        super(source);
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append("Incompatible casting source");
    }
}
