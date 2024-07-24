package dev.enjarai.trickster.spell.tricks.func;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellQueue;

import java.util.List;

public interface Forking {
    /**
     * @param ctx The context of the fork's parent. Apply pre-fork modifications if necessary, they are not discarded.
     * @param fragments The input fragments to this trick. If used as arguments to the fork, add them to the SpellContext.
     * @return A SpellQueue containing the fork's logic.
     */
    SpellQueue makeFork(SpellContext ctx, List<Fragment> fragments);
}
