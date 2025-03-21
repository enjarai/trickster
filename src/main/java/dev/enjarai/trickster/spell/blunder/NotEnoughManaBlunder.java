package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class NotEnoughManaBlunder extends TrickBlunderException {
    private final float required;

    public NotEnoughManaBlunder(Trick<?> source, float required) {
        super(source);
        this.required = required;
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append(Text.translatable(Trickster.MOD_ID + ".blunder.not_enough_mana.1"))
                .append(formatFloat(required)).append(".blunder.not_enough_mana.2");
    }
}
