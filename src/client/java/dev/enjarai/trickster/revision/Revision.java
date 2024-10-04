package dev.enjarai.trickster.revision;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellPart;

public interface Revision {
    /**
     * @return The pattern that triggers this revision.
     */
    Pattern pattern();

    /**
     * Applies this revision over the root spell part, replacing the root with this function's return.
     * @param ctx The context of the editor.
     * @param root The current root.
     * @param drawingPart The spell part that is being drawn in.
     * @return The new root spell part.
     */
    SpellPart apply(RevisionContext ctx, SpellPart root, SpellPart drawingPart);
}
