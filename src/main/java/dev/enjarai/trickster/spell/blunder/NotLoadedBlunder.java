package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;
import net.minecraft.util.math.BlockPos;

public class NotLoadedBlunder extends TrickBlunderException {
    public final BlockPos pos;

    public NotLoadedBlunder(Trick source, BlockPos pos) {
        super(source);
        this.pos = pos;
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append("Cannot access block (" + pos.toShortString() + ")");
    }
}
