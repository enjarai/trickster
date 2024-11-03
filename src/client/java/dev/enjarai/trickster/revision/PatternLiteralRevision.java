package dev.enjarai.trickster.revision;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.PatternGlyph;
import dev.enjarai.trickster.spell.SpellPart;

public class PatternLiteralRevision implements Revision {
    @Override
    public Pattern pattern() {
        return Pattern.of(8, 6, 1, 8);
    }

    @Override
    public SpellPart apply(RevisionContext ctx, SpellPart root, SpellPart drawingPart) {
        if (drawingPart.glyph instanceof PatternGlyph(Pattern pattern)) {
            drawingPart.glyph = pattern;
        }
        return root;
    }
}
