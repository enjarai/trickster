package dev.enjarai.trickster.spell.fleck;

import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import org.joml.Vector3fc;

import static dev.enjarai.trickster.EndecTomfoolery.VECTOR_3F_ENDEC;

public record LineFleck(Vector3fc pos, Vector3fc pos2) implements Fleck {
    static StructEndec<LineFleck> ENDEC = StructEndecBuilder.of(
            VECTOR_3F_ENDEC.fieldOf("pos", LineFleck::pos),
            VECTOR_3F_ENDEC.fieldOf("pos2", LineFleck::pos2),
            LineFleck::new
    );

    @Override
    public FleckType<?> type() {
        return FleckType.LINE;
    }
}
