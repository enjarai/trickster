package dev.enjarai.trickster.revision;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellPart;

public class CreateOuterSubcircleRevision implements Revision {
    @Override
    public Pattern pattern() {
        return Pattern.of(6, 3, 0, 4, 8);
    }

    @Override
    public SpellPart apply(RevisionContext ctx, SpellPart root, SpellPart drawingPart) {
        if (drawingPart != root) {
            if (root.glyph == drawingPart) {
                root.subParts.add(new SpellPart());
            } else {
                drawingPart.setSubPartInTree(current -> {
                    current.subParts.add(new SpellPart());
                    return current;
                }, root, true);
            }
        }

        return root;
    }
}
