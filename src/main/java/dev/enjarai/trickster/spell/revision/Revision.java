package dev.enjarai.trickster.spell.revision;

import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.SpellView;
import dev.enjarai.trickster.spell.Pattern;

import java.util.function.Consumer;

public interface Revision {
    /**
     * @return The pattern that triggers this revision.
     */
    Pattern pattern();

    /**
     * Applies this revision over the spell view it is drawn in. Use functions on the SpellView to modify the tree.
     *
     * @param ctx The context of the editor.
     * @param view The current view.
     */
    void apply(RevisionContext ctx, SpellView view);

    default void applyServer(RevisionContext ctx, SpellView view, Consumer<SpellPart> callback) {}
}
