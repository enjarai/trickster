package dev.enjarai.trickster.spell.tricks.blunder;


import dev.enjarai.trickster.spell.tricks.Trick;
import net.minecraft.text.MutableText;

public class ItemInvalidBlunder extends TrickBlunderException {
    public ItemInvalidBlunder(Trick source) {
        super(source);
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append("Invalid item");
    }
}