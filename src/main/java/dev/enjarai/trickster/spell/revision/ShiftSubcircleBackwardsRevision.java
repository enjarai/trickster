package dev.enjarai.trickster.spell.revision;

import dev.enjarai.trickster.spell.SpellView;
import dev.enjarai.trickster.spell.Pattern;

public class ShiftSubcircleBackwardsRevision implements Revision {
    @Override
    public Pattern pattern() {
        return Pattern.of(1, 0, 3);
    }

    @Override
    public void apply(RevisionContext ctx, SpellView view) {
        if (view.part.subParts.size() > 1) {
            var child = view.removeChild(0);
            view.addChild(view.children.size(), child);
        }
    }
}
