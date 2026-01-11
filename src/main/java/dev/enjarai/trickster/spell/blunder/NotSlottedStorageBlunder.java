package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;

public class NotSlottedStorageBlunder extends TrickBlunderException {
    public NotSlottedStorageBlunder(Trick<?> source) {
        super(source);
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append("Storage cannot be indexed by slots");
    }
}
