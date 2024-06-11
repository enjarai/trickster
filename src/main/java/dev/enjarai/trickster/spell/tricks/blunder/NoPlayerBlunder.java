package dev.enjarai.trickster.spell.tricks.blunder;

import dev.enjarai.trickster.spell.tricks.Trick;
import net.minecraft.text.MutableText;

public class NoPlayerBlunder extends TrickBlunderException {
    public NoPlayerBlunder(Trick source) {
        super(source);
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append("No player available");
    }
}
