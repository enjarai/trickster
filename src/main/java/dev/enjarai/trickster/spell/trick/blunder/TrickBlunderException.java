package dev.enjarai.trickster.spell.trick.blunder;

import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;

public abstract class TrickBlunderException extends BlunderException {
    public final Trick source;

    public TrickBlunderException(Trick source) {
        super();
        this.source = source;
    }

    public MutableText createMessage() {
        return source.getName().append(": ");
    }
}
