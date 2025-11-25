package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;

public class InvalidSlotBlunder extends TrickBlunderException {
    public InvalidSlotBlunder(Trick<?> source) {
        super(source);
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append("Invalid slot variant");
    }
}
