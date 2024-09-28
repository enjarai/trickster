package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;

public class NoFreeSpellSlotBlunder extends TrickBlunderException {
    public NoFreeSpellSlotBlunder(Trick source) {
        super(source);
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append("No free spell slots available");
    }
}
