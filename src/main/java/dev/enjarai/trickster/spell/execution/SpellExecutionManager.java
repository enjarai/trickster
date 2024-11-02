package dev.enjarai.trickster.spell.execution;

import java.util.List;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.execution.executor.DefaultSpellExecutor;
import dev.enjarai.trickster.spell.execution.executor.SpellExecutor;

public interface SpellExecutionManager {
    default boolean queue(SpellPart spell, List<Fragment> arguments) {
        return queue(new DefaultSpellExecutor(spell, arguments));
    }

    boolean queue(SpellExecutor executor);
    void killAll();
    void kill(int index);
}
