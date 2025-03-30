package dev.enjarai.trickster.spell.blunder;

import java.util.List;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class InvalidInputsBlunder extends TrickBlunderException {
    private final List<Fragment> given;

    public InvalidInputsBlunder(Trick<?> source, List<Fragment> fragments) {
        super(source);
        this.given = fragments;
    }

    @Override
    public MutableText createMessage() {
        var text = super.createMessage().append("Invalid inputs, the following signatures are valid for this trick:");

        for (var signature : source.getSignatures()) {
            text = text.append("\n- ").append(signature.asText());
        }

        return text.append("\n").append("The following inputs were given: ").append(inputText());
    }

    private Text inputText() {
        var text = Text.literal("");

        if (given.isEmpty()) {
            return text;
        }

        text = text.append(given.getFirst().asFormattedText());

        for (var fragment : given.subList(1, given.size())) {
            text = text.append(", ").append(fragment.asFormattedText());
        }

        return text;
    }
}
