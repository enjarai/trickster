package dev.enjarai.trickster.spell.revision;

import dev.enjarai.trickster.spell.SpellView;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellPart;

public class CreateOuterSubcircleRevision implements Revision {
    @Override
    public Pattern pattern() {
        return Pattern.of(6, 3, 0, 4, 8);
    }

    @Override
    public void apply(RevisionContext ctx, SpellView view) {
        if (view.parent != null && view.isInner) {
            view.parent.addChild(view.parent.children.size(), new SpellPart());
        }
    }
}
