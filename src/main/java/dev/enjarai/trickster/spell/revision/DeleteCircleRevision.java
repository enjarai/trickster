package dev.enjarai.trickster.spell.revision;

import dev.enjarai.trickster.spell.SpellView;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.PatternGlyph;

import java.util.List;

public class DeleteCircleRevision implements Revision {
    @Override
    public Pattern pattern() {
        return Pattern.of(0, 4, 8);
    }

    @Override
    public void apply(RevisionContext ctx, SpellView view) {
        if (view.children.isEmpty()) {
            if (view.parent != null) {
                view.delete();
            } else {
                view.replaceGlyph(new PatternGlyph());
                view.replaceChildren(List.of());
            }
        } else {
            var firstChild = view.children.getFirst();
            view.replace(firstChild.part);
        }
    }
}
