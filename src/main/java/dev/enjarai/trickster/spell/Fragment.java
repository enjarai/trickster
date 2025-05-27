package dev.enjarai.trickster.spell;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.component.FragmentComponent;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.execution.SerializedSpellInstruction;
import dev.enjarai.trickster.spell.execution.SpellInstructionType;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import io.netty.buffer.Unpooled;
import io.wispforest.endec.Endec;
import io.wispforest.endec.SerializationContext;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.format.bytebuf.ByteBufDeserializer;
import io.wispforest.endec.format.bytebuf.ByteBufSerializer;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.ArrayUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public non-sealed interface Fragment extends EvaluationResult, SpellInstruction {
    int MAX_WEIGHT = 64000;
    Text TRUNCATED_VALUE_TEXT = Text.literal(" [...]")
            .setStyle(
                    Style.EMPTY
                            .withColor(Formatting.RED)
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable(Trickster.MOD_ID + ".text.misc.value_truncated")))
            );
    @SuppressWarnings("unchecked")
    StructEndec<Fragment> ENDEC = EndecTomfoolery.lazyStruct(
            () -> (StructEndec<Fragment>) Endec.dispatchedStruct(
                    FragmentType::endec,
                    Fragment::type,
                    Endec.ifAttr(
                            EndecTomfoolery.UBER_COMPACT_ATTRIBUTE, EndecTomfoolery.protocolVersionAlternatives(
                                    Map.of(
                                            (byte) 1, FragmentType.INT_ID_ENDEC,
                                            (byte) 2, FragmentType.INT_ID_ENDEC,
                                            (byte) 3, FragmentType.INT_ID_ENDEC
                                    ),
                                    MinecraftEndecs.ofRegistry(FragmentType.REGISTRY)
                            )
                    )
                            .orElse(MinecraftEndecs.ofRegistry(FragmentType.REGISTRY))
            )
    );
    Endec<Fragment> COMPACT_ENDEC = EndecTomfoolery.withAlternative(
            Endec.BYTES.xmap(Fragment::fromBytes, Fragment::toBytes),
            ENDEC
    );

    FragmentType<?> type();

    Text asText();

    default Text asFormattedText() {
        var text = type().color().isPresent() ? asText().copy().withColor(type().color().getAsInt()) : asText().copy();

        // var hoverStack = ModItems.SCROLL_AND_QUILL.getDefaultStack();
        // hoverStack.set(DataComponentTypes.ITEM_NAME, text.copy());
        // hoverStack.set(ModComponents.FRAGMENT, new FragmentComponent(new SpellPart(this)));
        // text.styled(s -> s.withHoverEvent(new HoverEvent(
        //         HoverEvent.Action.SHOW_ITEM,
        //         new HoverEvent.ItemStackContent(hoverStack)
        // )));

        var siblings = text.getSiblings();
        var size = siblings.size();
        var newSiblings = new ArrayList<>(siblings).subList(0, Math.min(size, 100));
        siblings.clear();
        siblings.addAll(newSiblings);
        return text.append(
                size != newSiblings.size()
                        ? TRUNCATED_VALUE_TEXT
                        : Text.of("")
        );
    }

    default boolean asBoolean() {
        return true;
    }

    default boolean fuzzyEquals(Fragment other) {
        return equals(other);
    }

    default int fuzzyHash() {
        return hashCode();
    }

    default EvaluationResult activateAsGlyph(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return this;
    }

    @Override
    default SerializedSpellInstruction asSerialized() {
        return new SerializedSpellInstruction(SpellInstructionType.FRAGMENT, this);
    }

    /**
     * Potentially recursively remove ephemeral values from this fragment. May return
     * 
     * <pre>
     * this
     * </pre>
     * 
     * or any other new fragment. Potentially results in cloning the entire fragment if required.
     */
    default Fragment applyEphemeral() {
        return this;
    }

    /**
     * The weight of this fragment in terms of memory footprint. If possible, should be *roughly* equivalent to the amount of bytes in the fields of this fragment.
     */
    int getWeight();

    default Optional<BiFunction<SpellContext, List<Fragment>, EvaluationResult>> getActivator() {
        return Optional.of(this::activateAsGlyph);
    }

    byte[] GZIP_HEADER = new byte[] { 0x1f, (byte) 0x8b, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xff };

    default String toBase64() {
        return Base64.getEncoder().encodeToString(toBytes());
    }

    default byte[] toBytes() {
        var buf = Unpooled.buffer();
        buf.writeByte(4); // Protocol version
        ENDEC.encode(
                SerializationContext.empty().withAttributes(
                        EndecTomfoolery.UBER_COMPACT_ATTRIBUTE,
                        EndecTomfoolery.PROTOCOL_VERSION_ATTRIBUTE.instance((byte) 4)
                ),
                ByteBufSerializer.of(buf), this
        );

        var byteStream = new ByteArrayOutputStream(buf.writerIndex());
        try (byteStream) {
            try (GZIPOutputStream zipStream = new GZIPOutputStream(byteStream)) {
                buf.readBytes(zipStream, buf.writerIndex());
            }
        } catch (IOException e) {
            buf.release();
            throw new RuntimeException("Fragment encoding broke. what.");
        }

        var bytes = byteStream.toByteArray();
        byte[] result;
        try {
            result = Arrays.copyOfRange(bytes, 10, bytes.length);
        } catch (Throwable e) {
            buf.release();
            throw e;
        }

        buf.release();
        return result;
    }

    static Fragment fromBase64(String string) {
        return fromBytes(Base64.getDecoder().decode(string.strip()));
    }

    static Fragment fromBytes(byte[] bytes) {
        var buf = Unpooled.buffer();

        var byteStream = new ByteArrayInputStream(ArrayUtils.addAll(GZIP_HEADER, bytes));
        try (byteStream) {
            try (GZIPInputStream zipStream = new GZIPInputStream(byteStream)) {
                buf.writeBytes(zipStream.readAllBytes());
            }
        } catch (IOException e) {
            buf.release();
            throw new RuntimeException("Fragment decoding broke. what.");
        }

        var protocolVersion = buf.readByte();
        if (protocolVersion < 3) {
            return SpellPart.fromBytesOld(protocolVersion, buf);
        } else {
            Fragment result;
            try {
                result = ENDEC.decode(
                        SerializationContext.empty().withAttributes(
                                EndecTomfoolery.UBER_COMPACT_ATTRIBUTE,
                                EndecTomfoolery.PROTOCOL_VERSION_ATTRIBUTE.instance(protocolVersion)
                        ),
                        ByteBufDeserializer.of(buf)
                );
            } finally {
                buf.release();
            }

            return result;
        }
    }
}
