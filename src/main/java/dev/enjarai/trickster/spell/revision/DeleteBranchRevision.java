package dev.enjarai.trickster.spell.revision;

import dev.enjarai.trickster.spell.SpellView;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.PatternGlyph;

import java.util.List;

public class DeleteBranchRevision implements Revision {
    @Override
    public Pattern pattern() {
        return Pattern.of(0, 4, 8, 5, 2, 1, 0, 3, 6, 7, 8);
    }

    @Override
    public void apply(RevisionContext ctx, SpellView view) {
        if (view.parent != null) {
            view.comeOutAsTrans();
        } else {
            view.replaceGlyph(new PatternGlyph());
            view.replaceChildren(List.of());
        }
    }
}
