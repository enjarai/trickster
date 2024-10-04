package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;

public class AssertionBlunder extends TrickBlunderException {
    private final Fragment value;

    public AssertionBlunder(Trick source, Fragment value) {
        super(source);
        this.value = value;
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append("Assertion failed for value: ").append(value.asText());
    }
}
