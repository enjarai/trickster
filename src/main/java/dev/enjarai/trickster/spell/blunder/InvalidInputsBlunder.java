package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class InvalidInputsBlunder extends TrickBlunderException {
    public InvalidInputsBlunder(Trick<?> source) {
        super(source);
    }

    @Override
    public MutableText createMessage() {
        var text = super.createMessage().append(Text.translatable(Trickster.MOD_ID + ".blunder.invalid_inputs"));

        for (var signature : source.getSignatures()) {
            text = text.append("\n- ").append(signature.asText());
        }

        text.append("\n");
        return text;
    }
}
