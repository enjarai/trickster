package dev.enjarai.trickster.revision;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellPart;

public class DeleteBranchRevision implements Revision {
    @Override
    public Pattern pattern() {
        return Pattern.of(0, 4, 8, 5, 2, 1, 0, 3, 6, 7, 8);
    }

    @Override
    public SpellPart apply(RevisionContext ctx, SpellPart root, SpellPart drawingPart) {
        if (drawingPart == root)
            return new SpellPart();

        drawingPart.setSubPartInTree(current -> null, root, false);
        return root;
    }
}
