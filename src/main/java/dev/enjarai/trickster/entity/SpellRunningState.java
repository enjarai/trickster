package dev.enjarai.trickster.entity;

import com.mojang.serialization.Codec;
import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.spell.SpellPart;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.CodecUtils;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

import java.util.Map;
import java.util.Optional;

public class SpellRunningState {
    //uwu
    //(^ commiting war crimes in your codebase)
    private static final Map<String, StructEndec<? extends State>> endecMap = Map.of(
            "Idle", Idle.ENDEC,
            "Running", Running.ENDEC,
            "Error", Error.ENDEC
    );

    public static final Endec<State> ENDEC = Endec.dispatchedStruct( endecMap::get, State::id, Endec.STRING );
    public static final Codec<State> CODEC = CodecUtils.toCodec(ENDEC);
    public static final PacketCodec<PacketByteBuf, State> PACKET_CODEC = CodecUtils.toPacketCodec(ENDEC);
    public static final TrackedDataHandler<State> SPELL_DISPLAY_STATE = TrackedDataHandler.create(PACKET_CODEC);

    //look what we need to mimic a fraction of their power (rust users)
    public sealed interface State permits Idle, Running, Error {
        default boolean isError() { return false; }
        default boolean isIdle() { return false; }
        default boolean isRunning() { return false; }
        String id();

        default Optional<Text> getError() { return Optional.empty(); }
        default Optional<SpellPart> getSpell() { return Optional.empty(); }
    }

    public record Idle() implements State {
        public static final Idle instance = new Idle();
        public static final StructEndec<Idle> ENDEC = EndecTomfoolery.unit(instance);

        @Override
        public boolean isIdle() { return true; }

        @Override
        public String id() { return "Idle"; }
    }

    public record Running(SpellPart spellPart) implements State {
        public static final StructEndec<Running> ENDEC = StructEndecBuilder.of(
                SpellPart.ENDEC.flatFieldOf(Running::spellPart),
                Running::new
        );

        @Override
        public boolean isRunning() { return true; }

        @Override
        public Optional<SpellPart> getSpell() { return Optional.of(spellPart); }

        @Override
        public String id() { return "Running"; }
    }

    public record Error(Text error) implements State {
        public static final StructEndec<Error> ENDEC = StructEndecBuilder.of(
                CodecUtils.toEndec(TextCodecs.STRINGIFIED_CODEC).fieldOf("error", Error::error),
                Error::new
        );

        @Override
        public Optional<Text> getError() { return Optional.of(error); }

        @Override
        public boolean isError() { return true; }

        @Override
        public String id() { return "Error"; }
    }

    static {
        TrackedDataHandlerRegistry.register(SPELL_DISPLAY_STATE);
    }
}
