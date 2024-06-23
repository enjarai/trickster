package dev.enjarai.trickster.spell.tricks.blunder;

import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import net.minecraft.text.MutableText;

public class BlockUnoccupiedBlunder extends TrickBlunderException {
    public final VectorFragment pos;

    public BlockUnoccupiedBlunder(Trick source, VectorFragment pos) {
        super(source);
        this.pos = pos;
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append("Block ").append(pos.asText()).append(" unoccupied");
    }
}
