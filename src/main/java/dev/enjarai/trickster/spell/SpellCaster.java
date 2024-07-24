package dev.enjarai.trickster.spell;

import java.util.List;
import java.util.Optional;

public interface SpellCaster {
    default Optional<Fragment> executeSpell(SpellPart spell) {
        return executeSpell(spell, List.of());
    }

    default Optional<Fragment> executeSpell(SpellPart spell, List<Fragment> arguments) {
        var ctx = getDefaultCtx();
        ctx.pushPartGlyph(arguments);

        var result = executeSpell(ctx, spell);
        ctx.popPartGlyph();
        return result;
    }

    default Optional<Fragment> executeSpell(SpellContext ctx, SpellPart spell) {
        return getExecutionManager().execute(ctx, spell);
    }

    default void queueSpell(SpellContext ctx, SpellPart spell) {
        getExecutionManager().queue(ctx, spell);
    }

    SpellContext getDefaultCtx();

    SpellExecutionManager getExecutionManager();
}
