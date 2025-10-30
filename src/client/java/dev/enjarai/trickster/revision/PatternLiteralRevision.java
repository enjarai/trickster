package dev.enjarai.trickster.revision;

import dev.enjarai.trickster.SpellView;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.PatternGlyph;

public class PatternLiteralRevision implements Revision {
    @Override
    public Pattern pattern() {
        return Pattern.of(8, 6, 1, 8);
    }

    @Override
    public void apply(RevisionContext ctx, SpellView view) {
        if (view.part.glyph instanceof PatternGlyph(Pattern pattern)) {
            view.replaceGlyph(pattern);
        }
    }
}
