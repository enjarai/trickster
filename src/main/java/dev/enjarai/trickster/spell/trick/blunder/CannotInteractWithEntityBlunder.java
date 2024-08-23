package dev.enjarai.trickster.spell.trick.blunder;

import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.entity.Entity;
import net.minecraft.text.MutableText;

public class CannotInteractWithEntityBlunder extends TrickBlunderException {
    private final Entity entity;

    public CannotInteractWithEntityBlunder(Trick source, Entity entity) {
        super(source);
        this.entity = entity;
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append("Cannot interact with ").append(entity.getType().getName()).append(", restricted by operator.");
    }
}
