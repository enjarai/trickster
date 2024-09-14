package dev.enjarai.trickster.fleck;

import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import org.joml.Vector3dc;

import static dev.enjarai.trickster.EndecTomfoolery.VECTOR_3D_ENDEC;

public record LineFleck(Vector3dc pos1, Vector3dc pos2) implements Fleck {
    static StructEndec<LineFleck> ENDEC = StructEndecBuilder.of(
            VECTOR_3D_ENDEC.fieldOf("pos1", LineFleck::pos1),
            VECTOR_3D_ENDEC.fieldOf("pos2", LineFleck::pos2),
            LineFleck::new
    );

    @Override
    public FleckType<?> type() {
        return FleckType.LINE;
    }
}
