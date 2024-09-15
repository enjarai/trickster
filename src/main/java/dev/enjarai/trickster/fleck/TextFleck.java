package dev.enjarai.trickster.fleck;

import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.text.Text;
import org.joml.Vector3dc;
import org.joml.Vector3fc;

import static dev.enjarai.trickster.EndecTomfoolery.VECTOR_3F_ENDEC;

public record TextFleck(Vector3fc pos, Vector3fc facing, Text text) implements Fleck {
    static StructEndec<TextFleck> ENDEC = StructEndecBuilder.of(
            VECTOR_3F_ENDEC.fieldOf("pos", TextFleck::pos),
            VECTOR_3F_ENDEC.fieldOf("facing", TextFleck::facing),
            MinecraftEndecs.TEXT.fieldOf("text", TextFleck::text),
            TextFleck::new
    );

    @Override
    public FleckType<?> type() {
        return FleckType.SPELL;
    }
}
