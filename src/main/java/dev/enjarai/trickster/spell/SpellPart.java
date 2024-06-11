package dev.enjarai.trickster.spell;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import io.wispforest.endec.Endec;
import io.wispforest.owo.serialization.CodecUtils;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class SpellPart implements Glyph {
    public static final Codec<SpellPart> CODEC = Codec.recursive("spell_part", self -> RecordCodecBuilder.create(instance -> instance.group(
            Glyph.CODEC.fieldOf("glyph").forGetter(SpellPart::getGlyph),
            Codec.either(self, Codec.BOOL)
                    .xmap(e -> e.left(), o -> o.<Either<SpellPart, Boolean>>map(Either::left).orElse(Either.right(false)))
                    .listOf().fieldOf("sub_parts").forGetter(SpellPart::getSubParts)
    ).apply(instance, SpellPart::new)));
    public static final MapCodec<SpellPart> MAP_CODEC = MapCodec.assumeMapUnsafe(CODEC);
    public static final Endec<SpellPart> ENDEC = CodecUtils.ofCodec(CODEC);

    public Glyph glyph;
    public List<Optional<SpellPart>> subParts;

    public SpellPart(Glyph glyph, List<Optional<SpellPart>> subParts) {
        this.glyph = glyph;
        this.subParts = new ArrayList<>(subParts);
    }

    public SpellPart() {
        glyph = new PatternGlyph();
        subParts = new ArrayList<>();
    }

    @Override
    public Fragment activateGlyph(SpellContext ctx, List<Optional<Fragment>> fragments) throws BlunderException {
        if (fragments.isEmpty()) {
            return this;
        } else {
            ctx.pushPartGlyph(fragments.stream()
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList());
            var result = run(ctx);
            ctx.popPartGlyph();
            return result;
        }
    }

    public Fragment run(SpellContext ctx) throws BlunderException {
        var fragments = new ArrayList<Optional<Fragment>>();

        for (var part : subParts) {
            fragments.add(part.map(p -> p.run(ctx)));
        }

        return glyph.activateGlyph(ctx, fragments);
    }

    public Optional<Fragment> runSafely(SpellContext ctx) throws BlunderException {
        try {
            return Optional.of(run(ctx));
        } catch (BlunderException e) {
            ctx.getPlayer().ifPresent(player -> player.sendMessage(e.createMessage()));
        } catch (Exception e) {
            ctx.getPlayer().ifPresent(player -> player.sendMessage(
                    Text.literal("Uncaught exception in spell: " + e.getMessage())));
        }
        return Optional.empty();
    }

    public Glyph getGlyph() {
        return glyph;
    }

    public List<Optional<SpellPart>> getSubParts() {
        return subParts;
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
    public String asString() {
        return "TODO"; // TODO
    }

    @Override
    public BooleanFragment asBoolean() {
        return BooleanFragment.TRUE; // TODO
    }
}
