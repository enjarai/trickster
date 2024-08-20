package dev.enjarai.trickster.revision;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellPart;

public class ReplaceCircleWithOffHandRevision implements Revision {
    @Override
    public Pattern pattern() {
        return Pattern.of(4, 0, 1, 4, 2, 1);
    }

    @Override
    public SpellPart apply(RevisionContext ctx, SpellPart root, SpellPart drawingPart) {
        var offhand = ctx.getOtherHandSpell().deepClone();

        if (drawingPart == root)
            return offhand;

        drawingPart.setSubPartInTree(current -> offhand, root, false);
        return root;
    }
}
