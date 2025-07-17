package dev.enjarai.trickster.spell.trick.debug;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.EvaluationResult;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.Trick;

import java.util.List;

public class DebugLogTrick extends Trick<DebugLogTrick> {
    public DebugLogTrick() {
        super(Pattern.of(0, 4, 3, 6, 7, 8, 5, 4, 2));
    }

    @Override
    public EvaluationResult activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        Trickster.LOGGER.info("Debug log trick:");
        fragments.stream()
                .map(f -> f.asText().getString())
                .forEach(Trickster.LOGGER::info);
        return fragments.getFirst();
    }
}
