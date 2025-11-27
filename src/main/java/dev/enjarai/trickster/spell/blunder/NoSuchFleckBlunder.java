package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;

public class NoSuchFleckBlunder extends TrickBlunderException {
    public NoSuchFleckBlunder(Trick<?> source) {
        super(source);
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append("Fleck does not exist");
    }
}
