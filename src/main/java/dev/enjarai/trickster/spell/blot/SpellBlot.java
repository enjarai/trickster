package dev.enjarai.trickster.spell.blot;

import dev.enjarai.trickster.spell.SpellPart;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import org.joml.Vector2fc;

import static dev.enjarai.trickster.EndecTomfoolery.VECTOR_2F_ENDEC;

public record SpellBlot(Vector2fc pos, float size, SpellPart spell) implements Blot {
    static StructEndec<SpellBlot> ENDEC = StructEndecBuilder.of(
            VECTOR_2F_ENDEC.fieldOf("facing", SpellBlot::pos),
            Endec.FLOAT.fieldOf("size", SpellBlot::size),
            SpellPart.ENDEC.fieldOf("spell", SpellBlot::spell),
            SpellBlot::new
    );

    @Override
    public BlotType<?> type() {
        return BlotType.SPELL;
    }
}
