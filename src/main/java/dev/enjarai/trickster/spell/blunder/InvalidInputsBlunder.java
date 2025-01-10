package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;

public class InvalidInputsBlunder extends TrickBlunderException {
    public InvalidInputsBlunder(Trick<?> source) {
        super(source);
    }

    @Override
    public MutableText createMessage() {
        var text = super.createMessage().append("Invalid inputs, the following signatures are valid for this trick:");
        for (var handler : source.getHandlers()) {
            text = text.append("\n- ").append(handler.asText());
        }
        return text;
    }
}
