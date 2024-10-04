package dev.enjarai.trickster.revision;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellPart;

public class ReplaceOuterCircleRevision implements Revision {
    @Override
    public Pattern pattern() {
        return Pattern.of(1, 2, 4, 6);
    }

    @Override
    public SpellPart apply(RevisionContext ctx, SpellPart root, SpellPart drawingPart) {
        if (drawingPart != root) {
            if (root.glyph == drawingPart)
                return drawingPart;

            drawingPart.setSubPartInTree(current -> drawingPart, root, true);
        }

        return root;
    }
}
