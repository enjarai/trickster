package dev.enjarai.trickster.spell.revision;

import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.SpellView;
import dev.enjarai.trickster.spell.Pattern;

public class InsertParentCircleRevision implements Revision {
    @Override
    public Pattern pattern() {
        return Pattern.of(3, 0, 4, 8);
    }

    @Override
    public void apply(RevisionContext ctx, SpellView view) {
        var betweenView = SpellView.index(new SpellPart());
        view.replace(betweenView);
        betweenView.addChild(0, view);
    }
}
