package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;

public class CannotBypassWardBlunder extends TrickBlunderException {
    public CannotBypassWardBlunder(Trick<?> source) {
        super(source);
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append("Caster does not support bypassing wards");
    }
}
