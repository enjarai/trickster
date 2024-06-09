package dev.enjarai.trickster.spell.tricks.blunder;

import dev.enjarai.trickster.spell.tricks.Trick;
import net.minecraft.text.Text;

public abstract class BlunderException extends Exception {
    public final Trick source;

    public BlunderException(Trick source) {
        this.source = source;
    }

    public abstract Text createMessage();
}
