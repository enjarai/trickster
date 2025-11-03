package dev.enjarai.trickster.spell.revision;

import dev.enjarai.trickster.spell.SpellView;
import dev.enjarai.trickster.spell.Pattern;

import java.util.ArrayList;

public class ShiftSubcircleForwardsRevision implements Revision {
    @Override
    public Pattern pattern() {
        return Pattern.of(1, 2, 5);
    }

    @Override
    public void apply(RevisionContext ctx, SpellView view) {
        if (!view.part.isEmpty()) {
            var children = new ArrayList<>(view.part.subParts);
            children.addFirst(children.removeLast());
            view.replaceChildren(children);
        }
    }
}
