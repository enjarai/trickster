package dev.enjarai.trickster.spell;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.spell.execution.executor.DefaultSpellExecutor;
import dev.enjarai.trickster.spell.execution.executor.SpellExecutor;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.util.SpellUtils;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import io.netty.buffer.Unpooled;
import io.wispforest.endec.SerializationContext;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.format.bytebuf.ByteBufDeserializer;
import io.wispforest.endec.format.bytebuf.ByteBufSerializer;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ArrayUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class SpellPart implements Fragment {
    public static final StructEndec<SpellPart> ENDEC = EndecTomfoolery.recursive(self -> StructEndecBuilder.of(
            Fragment.ENDEC.fieldOf("glyph", SpellPart::getGlyph),
            EndecTomfoolery.protocolVersionAlternatives(
                    Map.of(
                            (byte) 1, self.listOf()
                    ),
                    EndecTomfoolery.withAlternative(SpellInstruction.STACK_ENDEC.xmap(
                            instructions -> SpellUtils.decodeInstructions(instructions, new Stack<>(), new Stack<>(), Optional.empty()),
                            SpellUtils::flattenNode
                    ), self).listOf()
            ).fieldOf("sub_parts", SpellPart::getSubParts),
            SpellPart::new
    ));

    public Fragment glyph;
    public List<SpellPart> subParts;

    public SpellPart(Fragment glyph, List<SpellPart> subParts) {
        this.glyph = glyph;
        this.subParts = new ArrayList<>(subParts);
    }

    public SpellPart(Fragment glyph) {
        this(glyph, new ArrayList<>());
    }

    public SpellPart() {
        this(new PatternGlyph());
    }

    @Override
    public Fragment activateAsGlyph(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        if (fragments.isEmpty()) {
            return Fragment.super.activateAsGlyph(ctx, fragments);
        } else {
            return makeFork(ctx, fragments).singleTickRun(ctx);
        }
    }

    @Override
    public boolean forks(SpellContext ctx, List<Fragment> args) {
        return !args.isEmpty();
    }

    @Override
    public SpellExecutor makeFork(SpellContext ctx, List<Fragment> args) throws BlunderException {
        return new DefaultSpellExecutor(this, ctx.state().recurseOrThrow(args));
    }

    /**
     * Since spell parts are mutable, this implementation deeply clones the entire object.
     */
    @Override
    public SpellPart applyEphemeral() {
        return new SpellPart(glyph.applyEphemeral(), subParts.stream()
                .map(SpellPart::applyEphemeral).toList());
    }

    @Override
    public int getWeight() {
        int weight = 8;
        weight += glyph.getWeight();

        for (SpellPart subPart : subParts) {
            weight += subPart.getWeight();
        }

        return weight;
    }

    public Fragment destructiveRun(SpellContext ctx) {
        var arguments = new ArrayList<Fragment>();

        for (var subpart : subParts) {
            arguments.add(subpart.destructiveRun(ctx));
        }

        var value = glyph.activateAsGlyph(ctx, arguments);

        if (!value.equals(VoidFragment.INSTANCE)) {
            if (glyph != value) {
                subParts.clear();
            }

            glyph = value;
        }

        return value;
    }

    public void buildClosure(Map<Fragment, Fragment> replacements) {
        subParts.forEach(part -> part.buildClosure(replacements));

        if (glyph instanceof SpellPart spellPart) {
            spellPart.buildClosure(replacements);
        } else if (replacements.containsKey(glyph)) {
            glyph = replacements.get(glyph);
        }
    }

    public boolean setSubPartInTree(Function<SpellPart, SpellPart> replace, SpellPart current, boolean targetIsInner) {
        if (current.glyph instanceof SpellPart part) {
            if (targetIsInner ? part.glyph == this : part == this) {
                var newPart = replace.apply(part);
                current.glyph = newPart == null ? new PatternGlyph() : newPart;
                return true;
            }

            if (setSubPartInTree(replace, part, targetIsInner)) {
                return true;
            }
        }

        int i = 0;
        for (var part : current.subParts) {
            if (targetIsInner ? part.glyph == this : part == this) {
                var newPart = replace.apply(part);
                if (newPart != null) {
                    current.subParts.set(i, newPart);
                } else {
                    current.subParts.remove(i);
                }
                return true;
            }

            if (setSubPartInTree(replace, part, targetIsInner)) {
                return true;
            }
            i++;
        }

        return false;
    }

    public Fragment getGlyph() {
        return glyph;
    }

    public List<SpellPart> getSubParts() {
        return subParts;
    }

    public boolean isEmpty() {
        return subParts.isEmpty() && glyph instanceof PatternGlyph patternGlyph && patternGlyph.pattern().isEmpty();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (SpellPart) obj;
        return Objects.equals(this.glyph, that.glyph) &&
                Objects.equals(this.subParts, that.subParts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(glyph, subParts);
    }

    @Override
    public String toString() {
        return "SpellPart[" +
                "glyph=" + glyph + ", " +
                "subParts=" + subParts + ']';
    }

    @Override
    public FragmentType<?> type() {
        return FragmentType.SPELL_PART;
    }

    @Override
    public Text asText() {
        var text = Text.literal("").append(glyph.asFormattedText()).append("{");
        for (int i = 0; i < subParts.size(); i++) {
            var subPart = subParts.get(i);
            if (i > 0) {
                text.append(", ");
            }
            text.append(subPart.asFormattedText());
        }
        text.append("}");
        return text;
    }

    @Override
    public Text asFormattedText() {
        return asText();
    }

    @Override
    public boolean asBoolean() {
        return glyph.asBoolean() || !subParts.isEmpty();
    }

    public SpellPart deepClone() {
        var glyph = this.glyph instanceof SpellPart spell ? spell.deepClone() : this.glyph;

        return new SpellPart(glyph, subParts.stream()
                .map(SpellPart::deepClone).collect(Collectors.toList()));
    }

    private static final byte[] base64Header = new byte[]{0x1f, (byte) 0x8b, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xff};

    public String toBase64() {
        var buf = Unpooled.buffer();
        buf.writeByte(2); // Protocol version
        ENDEC.encode(
                SerializationContext.empty().withAttributes(EndecTomfoolery.UBER_COMPACT_ATTRIBUTE),
                ByteBufSerializer.of(buf), this
        );

        var byteStream = new ByteArrayOutputStream(buf.writerIndex());
        try (byteStream) {
            try (GZIPOutputStream zipStream = new GZIPOutputStream(byteStream)) {
                buf.readBytes(zipStream, buf.writerIndex());
            }
        } catch (IOException e) {
            buf.release();
            throw new RuntimeException("Spell encoding broke. what.");
        }

        var bytes = byteStream.toByteArray();
        String result;
        try {
            result = Base64.getEncoder().encodeToString(Arrays.copyOfRange(bytes, 10, bytes.length));
        } catch (Throwable e) {
            buf.release();
            throw e;
        }

        buf.release();
        return result;
    }

    public static SpellPart fromBase64(String string) {
        var buf = Unpooled.buffer();

        var byteStream = new ByteArrayInputStream(ArrayUtils.addAll(base64Header, Base64.getDecoder().decode(string.strip())));
        try (byteStream) {
            try (GZIPInputStream zipStream = new GZIPInputStream(byteStream)) {
                buf.writeBytes(zipStream.readAllBytes());
            }
        } catch (IOException e) {
            buf.release();
            throw new RuntimeException("Spell decoding broke. what.");
        }

        var protocolVersion = buf.readByte();
        SpellPart result;
        try {
            result = ENDEC.decode(
                    SerializationContext.empty().withAttributes(
                            EndecTomfoolery.UBER_COMPACT_ATTRIBUTE,
                            EndecTomfoolery.PROTOCOL_VERSION_ATTRIBUTE.instance(protocolVersion)
                    ),
                    ByteBufDeserializer.of(buf)
            );
        } catch (Throwable e) {
            buf.release();
            throw e;
        }

        buf.release();
        return result;
    }
}
