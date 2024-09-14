package dev.enjarai.trickster.fleck;

import dev.enjarai.trickster.spell.SpellPart;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import org.joml.Vector3dc;

import static dev.enjarai.trickster.EndecTomfoolery.VECTOR_3D_ENDEC;

public record SpellFleck(Vector3dc pos, Vector3dc facing, SpellPart spell) implements Fleck {
    static StructEndec<SpellFleck> ENDEC = StructEndecBuilder.of(
            VECTOR_3D_ENDEC.fieldOf("pos", SpellFleck::pos),
            VECTOR_3D_ENDEC.fieldOf("facing", SpellFleck::facing),
            SpellPart.ENDEC.fieldOf("spell", SpellFleck::spell),
            SpellFleck::new
    );

    @Override
    public FleckType<?> type() {
        return FleckType.LINE;
    }
}
