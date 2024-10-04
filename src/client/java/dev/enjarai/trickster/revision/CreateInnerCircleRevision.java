package dev.enjarai.trickster.revision;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellPart;

public class CreateInnerCircleRevision implements Revision {
    @Override
    public Pattern pattern() {
        return Pattern.of(0, 4, 8, 5);
    }

    @Override
    public SpellPart apply(RevisionContext ctx, SpellPart root, SpellPart drawingPart) {
        drawingPart.glyph = new SpellPart();
        return root;
    }
}
