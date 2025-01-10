package dev.enjarai.trickster.spell.blot;

import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import org.joml.Vector2fc;

import static dev.enjarai.trickster.EndecTomfoolery.VECTOR_2F_ENDEC;

public record LineBlot(Vector2fc pos, Vector2fc pos2) implements Blot {
    static StructEndec<LineBlot> ENDEC = StructEndecBuilder.of(
            VECTOR_2F_ENDEC.fieldOf("pos", LineBlot::pos),
            VECTOR_2F_ENDEC.fieldOf("pos2", LineBlot::pos2),
            LineBlot::new
    );

    @Override
    public BlotType<?> type() {
        return BlotType.LINE;
    }
}
