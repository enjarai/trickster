package dev.enjarai.trickster.revision;

import dev.enjarai.trickster.SpellView;
import dev.enjarai.trickster.spell.Pattern;

public class ReplaceGlyphWithOffHandRevision implements Revision {
    @Override
    public Pattern pattern() {
        return Pattern.of(1, 2, 4, 1, 0, 4, 7);
    }

    @Override
    public void apply(RevisionContext ctx, SpellView view) {
        //TODO: get other hand fragment!
        //        view.replaceGlyph(ctx.getOtherHandSpell().deepClone());
    }
}
