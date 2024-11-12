package dev.enjarai.trickster.util;

import io.wispforest.endec.Deserializer;
import io.wispforest.endec.Endec;
import io.wispforest.endec.SelfDescribedDeserializer;
import io.wispforest.endec.SelfDescribedSerializer;
import io.wispforest.endec.SerializationContext;
import io.wispforest.endec.Serializer;

//TODO: fix the need for a self-described (de)serializer (VERY IMPORTANT) -- Aurora Dawn
// maybe it'll be better than withAlternative eventually...
public final class BackwardCompatibleEndec<T, A extends T> implements Endec<T> {
    private final Endec<T> primary;
    private final Endec<A> alternative;

    public BackwardCompatibleEndec(Endec<T> primary, Endec<A> alternative) {
        this.primary = primary;
        this.alternative = alternative;
    }

    @Override
    public void encode(SerializationContext ctx, Serializer<?> serializer, T value) {
        if (serializer instanceof SelfDescribedSerializer<?>) {
            primary.encode(ctx, serializer, value);
        } else throw new IllegalStateException("Serializer must be self-described");
    }

    @Override
    public T decode(SerializationContext ctx, Deserializer<?> deserializer) {
        if (deserializer instanceof SelfDescribedDeserializer<?>) {
            T result = null;

            try {
                result = deserializer.tryRead(d -> this.primary.decode(ctx, d));
            } catch (Exception ignore) {}

            if (result != null)
                return result;

            try {
                result = deserializer.tryRead(d -> this.alternative.decode(ctx, d));
            } catch (Exception ignore) {}

            if (result != null)
                return result;

            throw new IllegalStateException("Neither primary nor alternative read successfully");
        } else throw new IllegalStateException("Deserializer must be self-described");
    }
}
