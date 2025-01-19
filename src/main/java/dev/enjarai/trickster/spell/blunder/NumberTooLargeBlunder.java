package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class NumberTooLargeBlunder extends TrickBlunderException {
    private final int maximum;

    public NumberTooLargeBlunder(Trick<?> source, int maximum) {
        super(source);
        this.maximum = maximum;
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append(Text.translatable(Trickster.MOD_ID + ".blunder.number_too_large", maximum));
    }
}
