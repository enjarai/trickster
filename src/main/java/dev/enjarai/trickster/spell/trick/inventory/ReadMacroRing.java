package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.item.component.FragmentComponent;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.PatternGlyph;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.NoPlayerBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;

import java.util.Optional;

public class ReadMacroRing extends Trick<ReadMacroRing> {
    public ReadMacroRing() {
        super(Pattern.of(1, 2, 5, 8, 7, 6, 3, 0, 1, 5, 7, 3, 1, 4, 7), Signature.of(ReadMacroRing::run, FragmentType.PATTERN.mappedTo(FragmentType.SPELL_PART.retType()).optionalOfRet()));
    }

    public Optional<HashMap<PatternGlyph, SpellPart>> run(SpellContext ctx) throws BlunderException {
        return FragmentComponent.getUserMergedMap(ctx.source().getPlayer().orElseThrow(() -> new NoPlayerBlunder(this)), "ring")
                .map(ReadMacroRing::collectMap);
    }

    private static HashMap<PatternGlyph, SpellPart> collectMap(HashMap<Pattern, SpellPart> hamt) {
        return hamt.map((k, v) -> new Tuple2<>(new PatternGlyph(k), v));
    }
}
