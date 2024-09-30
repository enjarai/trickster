package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.text.MutableText;

public class EntityAlreadyStoredBlunder extends TrickBlunderException {
    public EntityAlreadyStoredBlunder(Trick source) {
        super(source);
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append("There is already an entity stored in the offhand item");
    }
}
