package dev.enjarai.trickster.fleck;

import dev.enjarai.trickster.spell.fragment.NumberFragment;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import org.joml.Vector3fc;

import static dev.enjarai.trickster.EndecTomfoolery.VECTOR_3F_ENDEC;

public record LineFleck(Vector3fc pos, Vector3fc pos2, float size) implements Fleck, ScalableFleck {

    static StructEndec<LineFleck> ENDEC = StructEndecBuilder.of(
            VECTOR_3F_ENDEC.fieldOf("pos", LineFleck::pos),
            VECTOR_3F_ENDEC.fieldOf("pos2", LineFleck::pos2),
            Endec.FLOAT.fieldOf("size", LineFleck::size),
            LineFleck::new
    );

    @Override
    public FleckType<?> type() {
        return FleckType.LINE;
    }

    @Override
    public ScalableFleck scaleFleck(NumberFragment scale) {
        return new LineFleck(
                pos,
                pos2,
                (float) (scale.number())
        );
    }
}
