package dev.enjarai.trickster.spell.tricks.func;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.execution.SpellExecutor;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;

import java.util.List;

public interface ForkingTrick {
    /**
     * @param fragments The input fragments to this trick. If used as arguments to the fork, add them to the SpellSource.
     * @return A SpellExecutor containing the fork's logic.
     */
    SpellExecutor makeFork(SpellContext ctx, List<Fragment> fragments) throws BlunderException;
}
