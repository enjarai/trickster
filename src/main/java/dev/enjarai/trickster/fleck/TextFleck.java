package dev.enjarai.trickster.fleck;

import dev.enjarai.trickster.EndecTomfoolery;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.text.Text;
import org.joml.Vector3dc;

import java.util.Optional;
import java.util.UUID;

import static dev.enjarai.trickster.EndecTomfoolery.VECTOR_3D_ENDEC;

public record TextFleck(Vector3dc pos, Vector3dc facing, Text text) implements Fleck {
    static StructEndec<TextFleck> ENDEC = StructEndecBuilder.of(
            VECTOR_3D_ENDEC.fieldOf("pos", TextFleck::pos),
            VECTOR_3D_ENDEC.fieldOf("facing", TextFleck::facing),
            MinecraftEndecs.TEXT.fieldOf("text", TextFleck::text),
            TextFleck::new
    );

    @Override
    public FleckType<?> type() {
        return FleckType.SPELL;
    }
}
