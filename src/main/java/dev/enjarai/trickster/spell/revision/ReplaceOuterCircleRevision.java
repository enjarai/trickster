package dev.enjarai.trickster.spell.revision;

import dev.enjarai.trickster.spell.SpellView;
import dev.enjarai.trickster.spell.Pattern;

public class ReplaceOuterCircleRevision implements Revision {
    @Override
    public Pattern pattern() {
        return Pattern.of(1, 2, 4, 6);
    }

    @Override
    public void apply(RevisionContext ctx, SpellView view) {
        if (view.parent != null) {
            view.parent.replace(view);
        }
    }
}
