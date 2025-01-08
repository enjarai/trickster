package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class InvalidArgumentsBlunder extends TrickBlunderException {
    public InvalidArgumentsBlunder(Trick<?> source) {
        super(source);
    }

    @Override
    public MutableText createMessage() {
        return Text.literal("Arguments do not match any signature");
    }
}
