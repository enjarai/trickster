package dev.enjarai.trickster.spell.trick.tree;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.PatternGlyph;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

public class EscapePatternTrick extends Trick<EscapePatternTrick> {
    public EscapePatternTrick() {
        super(Pattern.of(1, 5, 7, 3, 1, 4, 3), Signature.of(FragmentType.PATTERN, EscapePatternTrick::run, FragmentType.PATTERN_LITERAL));
    }

    public Pattern run(SpellContext ctx, PatternGlyph pattern) throws BlunderException {
        return pattern.pattern();
    }
}
