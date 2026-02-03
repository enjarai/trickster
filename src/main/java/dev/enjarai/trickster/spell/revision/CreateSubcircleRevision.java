package dev.enjarai.trickster.spell.revision;

import dev.enjarai.trickster.spell.SpellView;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellPart;

public class CreateSubcircleRevision implements Revision {
    @Override
    public Pattern pattern() {
        return Pattern.of(0, 4, 8, 7);
    }

    @Override
    public void apply(RevisionContext ctx, SpellView view) {
        view.addChild(view.children.size(), new SpellPart());
    }
}
