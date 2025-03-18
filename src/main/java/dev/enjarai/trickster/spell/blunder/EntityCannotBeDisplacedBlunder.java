package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.entity.Entity;
import net.minecraft.text.MutableText;

public class EntityCannotBeDisplacedBlunder extends TrickBlunderException {
    private final Entity entity;

    public EntityCannotBeDisplacedBlunder(Trick<?> source, Entity entity) {
        super(source);
        this.entity = entity;
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append("Cannot displace ").append(entity.getType().getName());
    }
}
