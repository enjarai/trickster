package dev.enjarai.trickster.spell.revision;

import dev.enjarai.trickster.spell.SpellView;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellPart;

import java.util.ArrayList;

public class CreateOuterSubcircleRevision implements Revision {
    @Override
    public Pattern pattern() {
        return Pattern.of(6, 3, 0, 4, 8);
    }

    @Override
    public void apply(RevisionContext ctx, SpellView view) {
        if (view.parent != null && view.isInner) {
            var children = new ArrayList<>(view.parent.part.subParts);
            children.add(new SpellPart());
            view.parent.replaceChildren(children);
        }
    }
}
