package dev.enjarai.trickster.revision;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellPart;

public class DeleteCircleRevision implements Revision {
    @Override
    public Pattern pattern() {
        return Pattern.of(0, 4, 8);
    }

    @Override
    public SpellPart apply(RevisionContext ctx, SpellPart root, SpellPart drawingPart) {
        var firstSubpart = drawingPart.getSubParts().stream().findFirst();

        if (drawingPart == root)
            return firstSubpart.orElse(new SpellPart());

        drawingPart.setSubPartInTree(current -> firstSubpart.orElse(null), root, false);
        return root;
    }
}
