package dev.enjarai.trickster.spell;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.fragment.ZalgoFragment;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import io.wispforest.endec.Endec;
import io.wispforest.owo.serialization.CodecUtils;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class SpellPart implements Fragment {
    public static final MapCodec<SpellPart> MAP_CODEC = MapCodec.recursive("spell_part", self -> RecordCodecBuilder.mapCodec(instance -> instance.group(
            Fragment.CODEC.get().fieldOf("glyph").forGetter(SpellPart::getGlyph),
            Codec.either(self, Codec.BOOL)
                    .xmap(e -> e.left(), o -> o.<Either<SpellPart, Boolean>>map(Either::left).orElse(Either.right(false)))
                    .listOf().fieldOf("sub_parts").forGetter(SpellPart::getSubParts)
    ).apply(instance, SpellPart::new)));
    public static final Codec<SpellPart> CODEC = MAP_CODEC.codec();
    public static final Endec<SpellPart> ENDEC = CodecUtils.toEndec(CODEC);

    public Fragment glyph;
    public List<Optional<SpellPart>> subParts;

    public SpellPart(Fragment glyph, List<Optional<SpellPart>> subParts) {
        this.glyph = glyph;
        this.subParts = new ArrayList<>(subParts);
    }

    public SpellPart() {
        glyph = new PatternGlyph();
        subParts = new ArrayList<>();
    }

    @Override
    public Fragment activateAsGlyph(SpellContext ctx, List<Optional<Fragment>> fragments) throws BlunderException {
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

        var value = glyph.activateAsGlyph(ctx, fragments);

        if (ctx.isDestructive() && !value.equals(VoidFragment.INSTANCE)) {
            if (glyph != value) {
                subParts.clear();
            }
            glyph = value;
        }

        return value;
    }

    public Optional<Fragment> runSafely(SpellContext ctx, Consumer<Text> onError) {
        try {
            return Optional.of(run(ctx));
        } catch (BlunderException e) {
            onError.accept(e.createMessage());
        } catch (Exception e) {
            onError.accept(Text.literal("Uncaught exception in spell: " + e.getMessage()));
        }
        return Optional.empty();
    }

    public Optional<Fragment> runSafely(SpellContext ctx) {
        return runSafely(ctx, err -> ctx.getPlayer().ifPresent(player -> player.sendMessage(err)));
    }

    public void brutallyMurderEphemerals() {
        subParts.forEach(part -> part.ifPresent(SpellPart::brutallyMurderEphemerals));

        if (glyph instanceof SpellPart spellPart) {
            spellPart.brutallyMurderEphemerals();
        } else {
            if (glyph.isEphemeral()) {
                glyph = new ZalgoFragment();
            }
        }
    }

    public Fragment getGlyph() {
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
    public Text asText() {
        return Text.of("TODO"); // TODO
    }

    @Override
    public BooleanFragment asBoolean() {
        return new BooleanFragment(glyph.asBoolean().bool() || !subParts.isEmpty());
    }

    public SpellPart deepClone() {
        var glyph = this.glyph instanceof SpellPart spell ? spell.deepClone() : this.glyph;

        return new SpellPart(glyph, subParts.stream()
                .map(o -> o.map(SpellPart::deepClone)).collect(Collectors.toList()));
    }
}
