package dev.enjarai.trickster.revision;

import dev.enjarai.trickster.SpellView;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellPart;

import java.util.ArrayList;

public class CreateSubcircleRevision implements Revision {
    @Override
    public Pattern pattern() {
        return Pattern.of(0, 4, 8, 7);
    }

    @Override
    public void apply(RevisionContext ctx, SpellView view) {
        var children = new ArrayList<>(view.part.subParts);
        children.add(new SpellPart());
        view.replaceChildren(children);
    }
}
