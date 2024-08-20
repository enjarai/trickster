package dev.enjarai.trickster.revision;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellPart;

public class ReplaceCircleWithCrowMindRevision implements Revision {
    @Override
    public Pattern pattern() {
        return Pattern.of(4, 1, 2, 4, 3, 6, 8, 5);
    }

    @Override
    public SpellPart apply(RevisionContext ctx, SpellPart root, SpellPart drawingPart) {
        return root; //TODO
    }
}
