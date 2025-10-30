package dev.enjarai.trickster.revision;

import dev.enjarai.trickster.SpellView;
import dev.enjarai.trickster.spell.Pattern;

import java.util.ArrayList;

public class ShiftSubcircleBackwardsRevision implements Revision {
    @Override
    public Pattern pattern() {
        return Pattern.of(1, 0, 3);
    }

    @Override
    public void apply(RevisionContext ctx, SpellView view) {
        if (!view.part.isEmpty()) {
            var children = new ArrayList<>(view.part.subParts);
            children.add(children.removeFirst());
            view.replaceChildren(children);
        }
    }
}
