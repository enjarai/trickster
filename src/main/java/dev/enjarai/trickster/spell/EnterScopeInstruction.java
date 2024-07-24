package dev.enjarai.trickster.spell;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public non-sealed class EnterScopeInstruction implements SpellInstruction {
    @Override
    public Optional<BiFunction<SpellContext, List<Fragment>, Fragment>> getActivator() {
        return Optional.empty();
    }
}
