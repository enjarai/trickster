package dev.enjarai.trickster.spell.trick.blunder;

import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.entity.Entity;
import net.minecraft.text.MutableText;

public class EntityCannotBeStoredBlunder extends TrickBlunderException {
    private final Entity entity;

    public EntityCannotBeStoredBlunder(Trick source, Entity entity) {
        super(source);
        this.entity = entity;
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append("Cannot store ").append(entity.getType().getName());
    }
}
