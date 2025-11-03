package dev.enjarai.trickster.spell.revision;

import dev.enjarai.trickster.spell.SpellView;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.PatternGlyph;

import java.util.List;

public class InsertParentCircleRevision implements Revision {
    @Override
    public Pattern pattern() {
        return Pattern.of(3, 0, 4, 8);
    }

    @Override
    public void apply(RevisionContext ctx, SpellView view) {
        var newPart = view.part.deepClone();
        view.replaceGlyph(new PatternGlyph());
        view.replaceChildren(List.of(newPart));
    }
}
