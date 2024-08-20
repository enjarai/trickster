package dev.enjarai.trickster.revision;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellPart;

public class SwapSubcircleRevision implements Revision {
    @Override
    public Pattern pattern() {
        return Pattern.of(2, 4, 3);
    }

    @Override
    public SpellPart apply(RevisionContext ctx, SpellPart root, SpellPart drawingPart) {
        if (drawingPart.subParts.size() > 1)
            drawingPart.subParts.addFirst(drawingPart.subParts.remove(1));

        return root;
    }
}
