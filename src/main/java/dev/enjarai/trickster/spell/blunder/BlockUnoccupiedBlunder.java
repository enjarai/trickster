package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;

public class BlockUnoccupiedBlunder extends TrickBlunderException {
    public final VectorFragment pos;

    public BlockUnoccupiedBlunder(Trick<?> source, VectorFragment pos) {
        super(source);
        this.pos = pos;
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append("Block at ").append(pos.asFormattedText()).append(" unoccupied");
    }
}
