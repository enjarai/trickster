package dev.enjarai.trickster.spell.execution;

import java.util.List;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.execution.executor.DefaultSpellExecutor;
import dev.enjarai.trickster.spell.SpellExecutor;

public interface SpellExecutionManager {
    default int queue(SpellPart spell, List<Fragment> arguments) {
        return queue(new DefaultSpellExecutor(spell, arguments));
    }

    int queue(SpellExecutor executor);
    boolean kill(int index);
    void killAll();
}
