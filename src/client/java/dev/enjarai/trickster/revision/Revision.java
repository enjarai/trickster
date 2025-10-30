package dev.enjarai.trickster.revision;

import dev.enjarai.trickster.SpellView;
import dev.enjarai.trickster.spell.Pattern;

public interface Revision {
    /**
     * @return The pattern that triggers this revision.
     */
    Pattern pattern();

    /**
     * Applies this revision over the root spell part, replacing the root with this function's return.
     *
     * @param ctx The context of the editor.
     * @param view The current root.
     */
    void apply(RevisionContext ctx, SpellView view);
}
