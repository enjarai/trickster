package dev.enjarai.trickster.revision;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellPart;

public class ExecuteOffHandRevision implements Revision {
    @Override
    public Pattern pattern() {
        return Pattern.of(4, 3, 0, 4, 5, 2, 4, 1);
    }

    @Override
    public SpellPart apply(RevisionContext ctx, SpellPart root, SpellPart drawingPart) {
        //TODO: make this independent of SpellPartWidget
        ctx.executeOffhand();
        return root;
    }
}
