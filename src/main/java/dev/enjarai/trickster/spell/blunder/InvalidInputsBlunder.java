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

        for (var signature : source.getSignatures()) {
            text = text.append("\n- ").append(signature.asText());
        }

        text.append("\n");
        return text;
    }
}
