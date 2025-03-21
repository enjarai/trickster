package dev.enjarai.trickster.spell.blunder;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.entity.Entity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class EntityCannotBeStoredBlunder extends TrickBlunderException {
    private final Entity entity;

    public EntityCannotBeStoredBlunder(Trick<?> source, Entity entity) {
        super(source);
        this.entity = entity;
    }

    @Override
    public MutableText createMessage() {
        return super.createMessage().append(Text.translatable(Trickster.MOD_ID + ".blunder.entity_cannot_be_stored", entity.getType().getName()));
    }
}
