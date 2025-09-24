package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;

public class NoSuchSpellSlotBlunder extends TrickBlunderException {
    public NoSuchSpellSlotBlunder(Trick<?> source) {
        super(source);
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append("No such spell slot present");
    }
}
