package dev.enjarai.trickster.spell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.enjarai.trickster.spell.execution.executor.SpellExecutor;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.fragment.ZalgoFragment;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import io.wispforest.endec.Endec;
import io.wispforest.owo.serialization.CodecUtils;
import net.minecraft.text.Text;

import java.util.*;
import java.util.stream.Collectors;

public final class SpellPart implements Fragment {
    public static final MapCodec<SpellPart> MAP_CODEC = MapCodec.recursive("spell_part", self -> RecordCodecBuilder.mapCodec(instance -> instance.group(
            Fragment.CODEC.get().fieldOf("glyph").forGetter(SpellPart::getGlyph),
            self.listOf().fieldOf("sub_parts").forGetter(SpellPart::getSubParts)
    ).apply(instance, SpellPart::new)));
    public static final Codec<SpellPart> CODEC = MAP_CODEC.codec();
    public static final Endec<SpellPart> ENDEC = CodecUtils.toEndec(CODEC);

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
        return new SpellExecutor(this, ctx.executionState().recurseOrThrow(args));
    }

    public void brutallyMurderEphemerals() {
        subParts.forEach(SpellPart::brutallyMurderEphemerals);

        if (glyph instanceof SpellPart spellPart) {
            spellPart.brutallyMurderEphemerals();
        } else {
            if (glyph.isEphemeral()) {
                glyph = new ZalgoFragment();
            }
        }
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

    public void buildClosure(Map<Pattern, Fragment> replacements) {
        subParts.forEach(part -> part.buildClosure(replacements));

        if (glyph instanceof SpellPart spellPart) {
            spellPart.buildClosure(replacements);
        } else if (glyph instanceof PatternGlyph patternGlyph) {
            var replacement = replacements.get(patternGlyph.pattern());
            if (replacement != null) {
                glyph = replacement;
            }
        }
    }

    public boolean setSubPartInTree(Optional<SpellPart> replacement, SpellPart current, boolean targetIsInner) {
        if (current.glyph instanceof SpellPart part) {
            if (targetIsInner ? part.glyph == this : part == this) {
                if (replacement.isPresent()) {
                    current.glyph = replacement.get();
                } else {
                    current.glyph = new PatternGlyph();
                }
                return true;
            }

            if (setSubPartInTree(replacement, part, targetIsInner)) {
                return true;
            }
        }

        int i = 0;
        for (var part : current.subParts) {
            if (targetIsInner ? part.glyph == this : part == this) {
                if (replacement.isPresent()) {
                    current.subParts.set(i, replacement.get());
                } else {
                    current.subParts.remove(i);
                }
                return true;
            }

            if (setSubPartInTree(replacement, part, targetIsInner)) {
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
    public BooleanFragment asBoolean() {
        return new BooleanFragment(glyph.asBoolean().bool() || !subParts.isEmpty());
    }

    public SpellPart deepClone() {
        var glyph = this.glyph instanceof SpellPart spell ? spell.deepClone() : this.glyph;

        return new SpellPart(glyph, subParts.stream()
                .map(SpellPart::deepClone).collect(Collectors.toList()));
    }
}
