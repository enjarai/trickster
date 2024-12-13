package dev.enjarai.trickster.fleck;

import dev.enjarai.trickster.spell.SpellPart;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import org.joml.Vector3fc;

import static dev.enjarai.trickster.EndecTomfoolery.VECTOR_3F_ENDEC;

public record SpellFleck(Vector3fc pos, Vector3fc facing, SpellPart spell) implements Fleck {

    static StructEndec<SpellFleck> ENDEC = StructEndecBuilder.of(
            VECTOR_3F_ENDEC.fieldOf("pos", SpellFleck::pos),
            VECTOR_3F_ENDEC.fieldOf("facing", SpellFleck::facing),
            SpellPart.ENDEC.fieldOf("spell", SpellFleck::spell),
            SpellFleck::new
    );

    @Override
    public FleckType<?> type() {
        return FleckType.SPELL;
    }
}
