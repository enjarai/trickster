package dev.enjarai.trickster.revision;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellPart;

public class ShiftSubcircleForwardsRevision implements Revision {
    @Override
    public Pattern pattern() {
        return Pattern.of(1, 2, 5);
    }

    @Override
    public SpellPart apply(RevisionContext ctx, SpellPart root, SpellPart drawingPart) {
        if (!drawingPart.subParts.isEmpty())
            drawingPart.subParts.addFirst(drawingPart.subParts.removeLast());

        return root;
    }
}
