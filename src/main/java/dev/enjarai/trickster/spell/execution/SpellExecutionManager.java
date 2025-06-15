package dev.enjarai.trickster.spell.execution;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.execution.executor.DefaultSpellExecutor;
import dev.enjarai.trickster.spell.SpellExecutor;

public interface SpellExecutionManager {
    default Optional<Integer> queue(SpellPart spell, List<Fragment> arguments) {
        return queue(new DefaultSpellExecutor(spell, arguments));
    }

    Optional<Integer> queue(SpellExecutor executor);

    boolean kill(int index);

    Optional<SpellExecutor> getSpellExecutor(int index);

    Optional<SpellPart> getSpell(int index);

    OptionalInt getSpellState(int index);

    void killAll();
}
