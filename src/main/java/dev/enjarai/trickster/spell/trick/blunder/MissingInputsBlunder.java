package dev.enjarai.trickster.spell.trick.blunder;

import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;

public class MissingInputsBlunder extends TrickBlunderException {
    public MissingInputsBlunder(Trick source) {
        super(source);
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append("Missing inputs");
    }
}
