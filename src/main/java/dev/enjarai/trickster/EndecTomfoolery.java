package dev.enjarai.trickster;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.util.UndashedUuid;
import io.netty.buffer.Unpooled;
import io.vavr.collection.HashMap;
import io.wispforest.endec.*;
import io.wispforest.endec.format.bytebuf.ByteBufDeserializer;
import io.wispforest.endec.format.bytebuf.ByteBufSerializer;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.CodecUtils;
import io.wispforest.owo.serialization.endec.EitherEndec;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.function.Function;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public class EndecTomfoolery {
    public static final Endec<BlockPos> ALWAYS_READABLE_BLOCK_POS = vectorEndec(Endec.INT, BlockPos::new, BlockPos::getX, BlockPos::getY, BlockPos::getZ);
    public static final Endec<UUID> UUID = Endec.STRING.xmap(UndashedUuid::fromStringLenient, java.util.UUID::toString);
    public static final SerializationAttribute.Marker UBER_COMPACT_ATTRIBUTE = SerializationAttribute.marker("uber_compact");
    public static final SerializationAttribute.WithValue<Byte> PROTOCOL_VERSION_ATTRIBUTE = SerializationAttribute.withValue("protocol_version");
    public static Endec<Vector3dc> VECTOR_3D_ENDEC = EndecTomfoolery.<Double, Vector3dc>vectorEndec(Endec.DOUBLE, Vector3d::new, Vector3dc::x, Vector3dc::y, Vector3dc::z);
    public static Endec<Vector3fc> VECTOR_3F_ENDEC = EndecTomfoolery.<Float, Vector3fc>vectorEndec(Endec.FLOAT, Vector3f::new, Vector3fc::x, Vector3fc::y, Vector3fc::z);
    public static final SerializationAttribute.Marker CODEC_SAFE = SerializationAttribute.marker("codec_safe");

    public static <C, V> Endec<V> vectorEndec(Endec<C> componentEndec, Function3<C, C, C, V> constructor, Function<V, C> xGetter, Function<V, C> yGetter, Function<V, C> zGetter) {
        return componentEndec.listOf().validate(ints -> {
            if (ints.size() != 3) {
                throw new IllegalStateException("Vector array must have three elements");
            }
        }).xmap(
                components -> constructor.apply(components.get(0), components.get(1), components.get(2)),
                vector -> List.of(xGetter.apply(vector), yGetter.apply(vector), zGetter.apply(vector))
        );
    }

    public static <T, A extends T> Endec<T> withAlternative(Endec<T> primary, Endec<A> alternative) {
        return new EitherEndec<T, A>(
                primary,
                alternative,
                false
        ).xmap(
                Either::unwrap,
                Either::left
        );
    }

    public static <T> Endec<Optional<T>> forcedSafeOptionalOf(Endec<T> endec) {
        return Endec.of(
                (ctx, serializer, value) -> {
                    try (var struct = serializer.struct()) {
                        struct.field("present", ctx, Endec.BOOLEAN, value.isPresent());
                        value.ifPresent(t -> struct.field("value", ctx, endec, t));
                    }
                },
                (ctx, deserializer) -> {
                    var struct = deserializer.struct();
                    //noinspection DataFlowIssue
                    if (struct.field("present", ctx, Endec.BOOLEAN)) {
                        //noinspection DataFlowIssue
                        return Optional.of(struct.field("value", ctx, endec));
                    } else {
                        return Optional.empty();
                    }
                }
        );
    }

    public static <T> Endec<Optional<T>> safeOptionalOf(Endec<T> endec) {
        return Endec.ifAttr(CODEC_SAFE, forcedSafeOptionalOf(endec)).orElse(endec.optionalOf());
    }

    public static <T> Endec<Stack<T>> stackOf(Endec<T> endec) {
        return endec.listOf().xmap(l -> {
            var stack = new Stack<T>();
            stack.addAll(l);
            return stack;
        }, ArrayList::new);
    }

    public static <T> StructEndec<T> funnyFieldOf(Endec<T> endec, String key) {
        return StructEndecBuilder.of(
                endec.fieldOf(key, Function.identity()),
                Function.identity()
        );
    }

    public static <T> Endec<T> asBytes(Endec<T> endec) {
        return Endec.of(
                (ctx, serializer, value) -> {
                    var buf = Unpooled.buffer();
                    endec.encode(
                            SerializationContext.empty().withAttributes(EndecTomfoolery.UBER_COMPACT_ATTRIBUTE),
                            ByteBufSerializer.of(buf), value
                    );

                    var byteStream = new ByteArrayOutputStream(buf.writerIndex());
                    try (byteStream) {
                        try (GZIPOutputStream zipStream = new GZIPOutputStream(byteStream)) {
                            buf.readBytes(zipStream, buf.writerIndex());
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to encode endec as bytes", e);
                    } finally {
                        buf.release();
                    }

                    var bytes = byteStream.toByteArray();
                    serializer.writeBytes(ctx, bytes);
                },
                (ctx, serializer) -> {
                    var buf = Unpooled.buffer();

                    var bytes = serializer.readBytes(ctx);
                    try (var byteStream = new ByteArrayInputStream(bytes);) {
                        try (var zipStream = new GZIPInputStream(byteStream)) {
                            buf.writeBytes(zipStream.readAllBytes());
                        }
                    } catch (Exception e) {
                        buf.release();
                        throw new RuntimeException("Failed to decode endec as bytes", e);
                    }

                    try {
                        return endec.decode(
                                SerializationContext.empty().withAttributes(EndecTomfoolery.UBER_COMPACT_ATTRIBUTE),
                                ByteBufDeserializer.of(buf)
                        );
                    } finally {
                        buf.release();
                    }
                }
        );
    }

    public static <T> Endec<T> withFallback(Endec<T> endec, Supplier<T> fallback) {
        return Endec.of(
                endec::encode,
                (ctx, serializer) -> {
                    try {
                        return endec.decode(ctx, serializer);
                    } catch (Exception e) {
                        return fallback.get();
                    }
                }
        );
    }

    public static <T> Codec<T> toCodec(Endec<T> endec) {
        return CodecUtils.toCodec(endec, SerializationContext.attributes(CODEC_SAFE));
    }

    public static <T> StructEndec<T> recursiveStruct(Function<StructEndec<T>, StructEndec<T>> wrapped) {
        return new RecursiveStructEndec<>(wrapped);
    }

    public static <T> StructEndec<T> lazyStruct(Supplier<StructEndec<T>> supplier) {
        return recursiveStruct(e -> supplier.get());
    }

    public static <T> Endec<T> recursive(Function<Endec<T>, Endec<T>> wrapped) {
        return new RecursiveEndec<>(wrapped);
    }

    public static <T> Endec<T> lazy(Supplier<Endec<T>> supplier) {
        return recursive(e -> supplier.get());
    }

    public static <T> StructEndec<T> unit(T value) {
        return new StructEndec<>() {
            @Override
            public void encodeStruct(SerializationContext ctx, Serializer<?> serializer, Serializer.Struct struct, T value) {
                // no-op
            }

            @Override
            public T decodeStruct(SerializationContext ctx, Deserializer<?> deserializer, Deserializer.Struct struct) {
                return value;
            }
        };
    }

    public static <T> StructEndec<T> unit(Supplier<T> value) {
        return new StructEndec<>() {
            @Override
            public void encodeStruct(SerializationContext ctx, Serializer<?> serializer, Serializer.Struct struct, T value) {
                // no-op
            }

            @Override
            public T decodeStruct(SerializationContext ctx, Deserializer<?> deserializer, Deserializer.Struct struct) {
                return value.get();
            }
        };
    }

    public static <T> Endec<T> protocolVersionAlternatives(Map<Byte, Endec<T>> protocols, Endec<T> defaultProtocol) {
        return new Endec<>() {
            @Override
            public void encode(SerializationContext ctx, Serializer<?> serializer, T value) {
                var protocolVersion = ctx.getAttributeValue(PROTOCOL_VERSION_ATTRIBUTE);
                if (protocolVersion == null) {
                    defaultProtocol.encode(ctx, serializer, value);
                    return;
                }

                var protocol = protocols.get(protocolVersion);
                if (protocol == null) {
                    defaultProtocol.encode(ctx, serializer, value);
                    return;
                }

                protocol.encode(ctx, serializer, value);
            }

            @Override
            public T decode(SerializationContext ctx, Deserializer<?> deserializer) {
                var protocolVersion = ctx.getAttributeValue(PROTOCOL_VERSION_ATTRIBUTE);
                if (protocolVersion == null) {
                    return defaultProtocol.decode(ctx, deserializer);
                }

                var protocol = protocols.get(protocolVersion);
                if (protocol == null) {
                    return defaultProtocol.decode(ctx, deserializer);
                }

                return protocol.decode(ctx, deserializer);
            }
        };
    }

    private static class RecursiveEndec<T> implements Endec<T> {
        private final Supplier<Endec<T>> wrapped;

        RecursiveEndec(Function<Endec<T>, Endec<T>> wrapped) {
            this.wrapped = Suppliers.memoize(() -> wrapped.apply(this));
        }

        @Override
        public void encode(SerializationContext ctx, Serializer<?> serializer, T value) {
            wrapped.get().encode(ctx, serializer, value);
        }

        @Override
        public T decode(SerializationContext ctx, Deserializer<?> deserializer) {
            return wrapped.get().decode(ctx, deserializer);
        }
    }

    private static class RecursiveStructEndec<T> implements StructEndec<T> {
        private final Supplier<StructEndec<T>> wrapped;

        RecursiveStructEndec(Function<StructEndec<T>, StructEndec<T>> wrapped) {
            this.wrapped = Suppliers.memoize(() -> wrapped.apply(this));
        }

        @Override
        public void encodeStruct(SerializationContext ctx, Serializer<?> serializer, Serializer.Struct struct, T value) {
            wrapped.get().encodeStruct(ctx, serializer, struct, value);
        }

        @Override
        public T decodeStruct(SerializationContext ctx, Deserializer<?> deserializer, Deserializer.Struct struct) {
            return wrapped.get().decodeStruct(ctx, deserializer, struct);
        }
    }

    public static <K, V> Endec<HashMap<K, V>> hamt(Endec<K> key, Endec<V> value) {
        return Endec.map(key, value).xmap(HashMap::ofAll, HashMap::toJavaMap);
    }
}
