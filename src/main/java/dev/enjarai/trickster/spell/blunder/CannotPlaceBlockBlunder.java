package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.block.Block;
import net.minecraft.text.MutableText;

public class CannotPlaceBlockBlunder extends TrickBlunderException {
    public final Block block;
    public final VectorFragment pos;

    public CannotPlaceBlockBlunder(Trick<?> source, Block block, VectorFragment pos) {
        super(source);
        this.block = block;
        this.pos = pos;
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append("Cannot place ").append(block.getName()).append(" at ").append(pos.asFormattedText());
    }
}
