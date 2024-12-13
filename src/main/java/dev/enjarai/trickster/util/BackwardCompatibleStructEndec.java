package dev.enjarai.trickster.util;

import io.wispforest.endec.Deserializer;
import io.wispforest.endec.SelfDescribedDeserializer;
import io.wispforest.endec.SelfDescribedSerializer;
import io.wispforest.endec.SerializationContext;
import io.wispforest.endec.Serializer;
import io.wispforest.endec.StructEndec;

//TODO: fix the need for a self-described (de)serializer -- Aurora Dawn
public final class BackwardCompatibleStructEndec<T, A extends T> implements StructEndec<T> {
    private final StructEndec<T> primary;
    private final StructEndec<A> alternative;

    public BackwardCompatibleStructEndec(StructEndec<T> primary, StructEndec<A> alternative) {
        this.primary = primary;
        this.alternative = alternative;
    }

    @Override
    public void encodeStruct(SerializationContext ctx, Serializer<?> serializer, Serializer.Struct struct, T value) {
        if (serializer instanceof SelfDescribedSerializer<?>) {
            primary.encodeStruct(ctx, serializer, struct, value);
        } else throw new IllegalStateException("Serializer must be self-described");
    }

    @Override
    public T decodeStruct(SerializationContext ctx, Deserializer<?> deserializer, Deserializer.Struct struct) {
        if (deserializer instanceof SelfDescribedDeserializer<?>) {
            T result = null;

            try {
                result = deserializer.tryRead(d -> this.primary.decodeStruct(ctx, d, struct));
            } catch (Exception ignore) {}

            if (result != null)
                return result;

            try {
                result = deserializer.tryRead(d -> this.alternative.decodeStruct(ctx, d, struct));
            } catch (Exception ignore) {}

            if (result != null)
                return result;

            throw new IllegalStateException("Neither primary nor alternative read successfully");
        } else throw new IllegalStateException("Deserializer must be self-described");
    }
}
