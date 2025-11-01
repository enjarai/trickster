package dev.enjarai.trickster.revision;

import dev.enjarai.trickster.SpellView;
import dev.enjarai.trickster.spell.Pattern;

public class WriteBranchToOffHandRevision implements Revision {
    @Override
    public Pattern pattern() {
        return Pattern.of(1, 4, 7, 6, 4, 8, 7);
    }

    @Override
    public void apply(RevisionContext ctx, SpellView view) {
        // TODO
        //        ctx.updateOffHandSpell(view.part);
    }
}
