package dev.enjarai.trickster.spell.trick.blunder;

import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;
import net.minecraft.util.math.BlockPos;

public class CannotEditBlockBlunder extends TrickBlunderException {
    public final BlockPos pos;

    public CannotEditBlockBlunder(Trick source, BlockPos pos) {
        super(source);
        this.pos = pos;
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append("Cannot edit block (" + pos.toShortString() + "), restricted by operator.");
    }
}
