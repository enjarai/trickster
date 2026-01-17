package dev.enjarai.trickster.spell;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.Collectors;
import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.execution.executor.DefaultSpellExecutor;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.util.FuzzyUtils;
import dev.enjarai.trickster.util.SpellUtils;
import io.netty.buffer.ByteBuf;
import io.wispforest.endec.SerializationContext;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.format.bytebuf.ByteBufDeserializer;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.text.Text;
import org.joml.Vector2d;

public final class SpellPart implements Fragment {
    public static final StructEndec<SpellPart> ENDEC = EndecTomfoolery.recursiveStruct(
        self -> StructEndecBuilder.of(
            Fragment.ENDEC.fieldOf("glyph", SpellPart::getGlyph),
            EndecTomfoolery.protocolVersionAlternatives(
                Map.of(
                    (byte) 1, self.listOf()
                ),
                EndecTomfoolery.withAlternative(
                    SpellInstruction.STACK_ENDEC.xmap(
                        instructions -> SpellUtils.decodeInstructions(instructions, new Stack<>(), new Stack<>(), Optional.empty()),
                        SpellUtils::flattenNode
                    ), self
                ).listOf()
            ).fieldOf("sub_parts", SpellPart::getSubParts),
            SpellPart::new
        )
    );

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
    public EvaluationResult activateAsGlyph(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        if (fragments.isEmpty()) {
            return Fragment.super.activateAsGlyph(ctx, fragments);
        } else {
            return new DefaultSpellExecutor(this, ctx.state().recurseOrThrow(fragments));
        }
    }

    /**
     * Since spell parts are mutable, this implementation deeply clones the entire object.
     */
    @Override
    public SpellPart applyEphemeral() {
        return new SpellPart(
            glyph.applyEphemeral(), subParts.stream()
                .map(SpellPart::applyEphemeral).toList()
        );
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

        var result = glyph.activateAsGlyph(ctx, arguments);
        Fragment value;

        if (result instanceof SpellExecutor executor) {
            value = executor.singleTickRun(ctx); //TODO: should account for ticks so far
        } else if (result instanceof Fragment fragment) {
            value = fragment;
        } else {
            throw new UnsupportedOperationException();
        }

        if (!value.equals(VoidFragment.INSTANCE)) {
            if (glyph != value) {
                subParts.clear();
            }

            glyph = value;
        }

        return value;
    }

    public SpellPart buildClosure(io.vavr.collection.Map<Fragment, Fragment> replacements) {
        if (replacements.containsKey(this) && replacements.get(this).get() instanceof SpellPart spellPart) {
            return spellPart;
        }

        subParts = new ArrayList<>(subParts.stream().map(part -> part.buildClosure(replacements)).toList());

        if (glyph instanceof SpellPart spellPart) {
            glyph = spellPart.buildClosure(replacements);
        } else if (replacements.containsKey(glyph)) {
            glyph = replacements.get(glyph).get();
        }

        return this;
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

        if (obj instanceof SpellPart that) {
            return Objects.equals(this.glyph, that.glyph)
                && Objects.equals(this.subParts, that.subParts);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(glyph, subParts);
    }

    @Override
    public boolean fuzzyEquals(Fragment other) {
        if (other == this) return true;

        if (other instanceof SpellPart that) {
            return this.glyph.fuzzyEquals(that.glyph)
                && FuzzyUtils.fuzzyEquals(this.subParts, that.subParts);
        }

        return false;
    }

    @Override
    public int fuzzyHash() {
        return Objects.hash(this.glyph.fuzzyHash(), FuzzyUtils.fuzzyHash(this.subParts));
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
            if (subPart.glyph instanceof PatternGlyph) {
                text.append("...");
            } else {
                text.append(subPart.glyph.asFormattedText());
            }
        }
        text.append("}");
        return text;
    }

    @Override
    public Text asFormattedText() {
        return asText();
    }

    public SpellPart deepClone() {
        var glyph = this.glyph instanceof SpellPart spell ? spell.deepClone() : this.glyph;

        return new SpellPart(
            glyph, subParts.stream()
                .map(SpellPart::deepClone).collect(Collectors.toList())
        );
    }

    public static SpellPart fromBytesOld(byte protocolVersion, ByteBuf buf) {
        SpellPart result;
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

    public int partCount() {
        return subParts.size();
    }

    public double subRadius(double radius) {
        return Math.min(radius / 2, radius / (double) ((this.partCount() + 1) / 2));
    }

    public double superRadius(double childRadius) {
        return Math.max(childRadius * 2, childRadius * (double) ((this.partCount() + 1) / 2));
    }

    public double subAngle(int index, double angleOffset) {
        return angleOffset + (2 * Math.PI) / this.partCount() * index - (Math.PI / 2);
    }

    public double superAngle(int index, double childAngleOffset) {
        return childAngleOffset - (2 * Math.PI) / this.partCount() * index + (Math.PI / 2);
    }

    public Vector2d subPosition(int index, double radius, double angleOffset) {
        double angle = this.subAngle(index, angleOffset);
        return new Vector2d(Math.cos(angle), Math.sin(angle)).mul(radius);
    }
}
