package dev.enjarai.trickster.revision;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellPart;

public class ReplaceGlyphWithOffHandRevision implements Revision {
    @Override
    public Pattern pattern() {
        return Pattern.of(1, 2, 4, 1, 0, 4, 7);
    }

    @Override
    public SpellPart apply(RevisionContext ctx, SpellPart root, SpellPart drawingPart) {
        drawingPart.glyph = ctx.getOtherHandSpell().deepClone();
        return root;
    }
}
