package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;

public class IndexOutOfBoundsBlunder extends TrickBlunderException {
    public final int index;

    public IndexOutOfBoundsBlunder(Trick source, int index) {
        super(source);
        this.index = index;
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append("Index out of bounds: ").append(formatInt(index));
    }
}
