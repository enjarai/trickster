package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class AtomicChunkTooLargeBlunder extends TrickBlunderException {
    public AtomicChunkTooLargeBlunder(Trick source) {
        super(source);
    }

    @Override
    public MutableText createMessage() {
        return Text.literal("Atomic spell is too large to be run in a single tick");
    }
}
