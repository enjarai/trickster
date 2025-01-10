package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;

public class NotEnoughManaBlunder extends TrickBlunderException {
    private final float required;

    public NotEnoughManaBlunder(Trick<?> source, float required) {
        super(source);
        this.required = required;
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append("Not enough mana, at least ")
                .append(formatFloat(required)).append(" kilogandalfs are required.");
    }
}
