package dev.enjarai.trickster.revision;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellPart;

public class WriteBranchToOffHandRevision implements Revision {
    @Override
    public Pattern pattern() {
        return Pattern.of(1, 4, 7, 6, 4, 8, 7);
    }

    @Override
    public SpellPart apply(RevisionContext ctx, SpellPart root, SpellPart drawingPart) {
        ctx.updateOtherHandSpell(drawingPart);
        return drawingPart;
    }
}
