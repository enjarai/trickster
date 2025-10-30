package dev.enjarai.trickster.revision;

import dev.enjarai.trickster.SpellView;
import dev.enjarai.trickster.spell.Pattern;

public class ReplaceCircleWithCrowMindRevision implements Revision {
    @Override
    public Pattern pattern() {
        return Pattern.of(4, 1, 2, 4, 3, 6, 8, 5);
    }

    @Override
    public void apply(RevisionContext ctx, SpellView view) {
        //TODO
    }
}
