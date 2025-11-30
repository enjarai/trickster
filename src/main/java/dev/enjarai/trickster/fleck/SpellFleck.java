package dev.enjarai.trickster.fleck;

import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import org.joml.Vector3fc;

import static dev.enjarai.trickster.EndecTomfoolery.VECTOR_3F_ENDEC;

public record SpellFleck(Vector3fc pos, Vector3fc facing, SpellPart spell, float size, float roll) implements Fleck, ScalableFleck, RollableFleck {

    static StructEndec<SpellFleck> ENDEC = StructEndecBuilder.of(
            VECTOR_3F_ENDEC.fieldOf("pos", SpellFleck::pos),
            VECTOR_3F_ENDEC.fieldOf("facing", SpellFleck::facing),
            SpellPart.ENDEC.fieldOf("spell", SpellFleck::spell),
            Endec.FLOAT.fieldOf("size", SpellFleck::size),
            Endec.FLOAT.fieldOf("roll", SpellFleck::roll),
            SpellFleck::new
    );

    @Override
    public FleckType<?> type() {
        return FleckType.SPELL;
    }

    @Override
    public RollableFleck rollFleck(float roll) {
        return new SpellFleck(
                pos,
                facing,
                spell,
                size,
                roll
        );
    }

    @Override
    public ScalableFleck scaleFleck(NumberFragment scale) {
        return new SpellFleck(
                pos,
                facing,
                spell,
                (float) (scale.number()),
                roll
        );
    }
}
