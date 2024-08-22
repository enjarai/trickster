package dev.enjarai.trickster.revision;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellPart;

public class InsertParentCircleRevision implements Revision {
    @Override
    public Pattern pattern() {
        return Pattern.of(3, 0, 4, 8);
    }

    @Override
    public SpellPart apply(RevisionContext ctx, SpellPart root, SpellPart drawingPart) {
        var newPart = new SpellPart();
        newPart.subParts.add(drawingPart);

        if (drawingPart == root)
            return newPart;

        drawingPart.setSubPartInTree(current -> newPart, root, false);
        return root;
    }
}
