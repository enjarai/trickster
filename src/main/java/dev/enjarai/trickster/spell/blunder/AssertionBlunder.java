package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class AssertionBlunder extends TrickBlunderException {
    private final Fragment value;

    public AssertionBlunder(Trick<?> source, Fragment value) {
        super(source);
        this.value = value;
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append(Text.translatable(Trickster.MOD_ID + ".blunder.assertion", value.asText()));
    }
}
