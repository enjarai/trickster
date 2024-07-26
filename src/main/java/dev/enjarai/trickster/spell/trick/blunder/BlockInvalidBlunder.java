package dev.enjarai.trickster.spell.trick.blunder;

import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;

public class BlockInvalidBlunder extends TrickBlunderException {
    public BlockInvalidBlunder(Trick source) {
        super(source);
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append("Invalid block type");
    }
}
