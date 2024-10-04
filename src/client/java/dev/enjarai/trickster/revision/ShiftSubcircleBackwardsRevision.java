package dev.enjarai.trickster.revision;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellPart;

public class ShiftSubcircleBackwardsRevision implements Revision {
    @Override
    public Pattern pattern() {
        return Pattern.of(1, 0, 3);
    }

    @Override
    public SpellPart apply(RevisionContext ctx, SpellPart root, SpellPart drawingPart) {
        if (!drawingPart.subParts.isEmpty())
            drawingPart.subParts.add(drawingPart.subParts.removeFirst());

        return root;
    }
}
