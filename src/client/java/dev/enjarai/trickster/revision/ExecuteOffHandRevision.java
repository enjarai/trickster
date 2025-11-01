package dev.enjarai.trickster.revision;

import dev.enjarai.trickster.SpellView;
import dev.enjarai.trickster.spell.Pattern;

public class ExecuteOffHandRevision implements Revision {
    @Override
    public Pattern pattern() {
        return Pattern.of(4, 3, 0, 4, 5, 2, 4, 1);
    }

    @Override
    public void apply(RevisionContext ctx, SpellView view) {
        //TODO: make this independent of SpellPartWidget
        // TODO implement and meow a lot
        //        ctx.executeOffhand();
    }
}
