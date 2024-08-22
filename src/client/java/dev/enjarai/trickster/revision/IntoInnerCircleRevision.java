package dev.enjarai.trickster.revision;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellPart;

public class IntoInnerCircleRevision implements Revision {
    @Override
    public Pattern pattern() {
        return Pattern.of(1, 0, 4, 8);
    }

    @Override
    public SpellPart apply(RevisionContext ctx, SpellPart root, SpellPart drawingPart) {
        var newPart = new SpellPart();
        newPart.glyph = drawingPart;

        if (drawingPart == root)
            return newPart;

        drawingPart.setSubPartInTree(current -> newPart, root, false);
        return root;
    }
}
