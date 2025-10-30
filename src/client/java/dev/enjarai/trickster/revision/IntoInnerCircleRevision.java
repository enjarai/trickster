package dev.enjarai.trickster.revision;

import dev.enjarai.trickster.SpellView;
import dev.enjarai.trickster.spell.Pattern;

import java.util.List;

public class IntoInnerCircleRevision implements Revision {
    @Override
    public Pattern pattern() {
        return Pattern.of(1, 0, 4, 8);
    }

    @Override
    public void apply(RevisionContext ctx, SpellView view) {
        var newPart = view.part.deepClone();
        view.replaceGlyph(newPart);
        view.replaceChildren(List.of());
    }
}
