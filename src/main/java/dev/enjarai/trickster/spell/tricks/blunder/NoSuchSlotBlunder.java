package dev.enjarai.trickster.spell.tricks.blunder;

import dev.enjarai.trickster.spell.tricks.Trick;
import net.minecraft.text.MutableText;

public class NoSuchSlotBlunder extends TrickBlunderException {
    public NoSuchSlotBlunder(Trick source) {
        super(source);
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append("Item slot does not exist");
    }
}
