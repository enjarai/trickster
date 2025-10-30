package dev.enjarai.trickster.revision;

import dev.enjarai.trickster.SpellView;
import dev.enjarai.trickster.spell.Pattern;

public class ReplaceCircleWithOffHandRevision implements Revision {
    @Override
    public Pattern pattern() {
        return Pattern.of(4, 0, 1, 4, 2, 1);
    }

    @Override
    public void apply(RevisionContext ctx, SpellView view) {
        var offhand = ctx.getOtherHandSpell().deepClone();

        view.replace(offhand);
    }
}
