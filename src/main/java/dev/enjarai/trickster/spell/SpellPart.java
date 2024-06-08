package dev.enjarai.trickster.spell;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

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
    public Fragment activateGlyph(SpellContext ctx, List<Optional<Fragment>> fragments) {
        ctx.pushPartGlyph(fragments);
        var result = run(ctx);
        ctx.popPartGlyph();

        return result;
    }

    public Fragment run(SpellContext ctx) {
        var fragments = new ArrayList<Optional<Fragment>>();

        for (var part : subParts) {
            fragments.add(part.map(p -> p.run(ctx)));
        }

        return glyph.activateGlyph(ctx, fragments);
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

}
