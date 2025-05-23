package dev.enjarai.trickster.spell.execution.executor;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellExecutor;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.execution.TickData;
import dev.enjarai.trickster.spell.execution.ExecutionState;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;

import java.util.List;
import java.util.Optional;

public class TryCatchSpellExecutor implements SpellExecutor {
    public static final StructEndec<TryCatchSpellExecutor> ENDEC = StructEndecBuilder.of(
            SpellExecutor.INTERNAL_ENDEC.fieldOf("try", e -> e.trySpell),
            SpellExecutor.INTERNAL_ENDEC.fieldOf("catch", e -> e.catchSpell),
            Endec.BOOLEAN.fieldOf("catching", e -> e.catching),
            TryCatchSpellExecutor::new
    );

    private final SpellExecutor trySpell;
    private final SpellExecutor catchSpell;
    private boolean catching = false;

    private TryCatchSpellExecutor(SpellExecutor trySpell, SpellExecutor catchSpell, boolean catching) {
        this.trySpell = trySpell;
        this.catchSpell = catchSpell;
        this.catching = catching;
    }

    public TryCatchSpellExecutor(SpellContext ctx, SpellPart trySpell, SpellPart catchSpell, List<Fragment> arguments) {
        this.trySpell = new DefaultSpellExecutor(trySpell, ctx.state().recurseOrThrow(arguments));
        this.catchSpell = new DefaultSpellExecutor(catchSpell, ctx.state().recurseOrThrow(arguments));
    }

    @Override
    public SpellExecutorType<?> type() {
        return SpellExecutorType.TRY_CATCH;
    }

    @Override
    public SpellPart spell() {
        return catching ? catchSpell.spell() : trySpell.spell();
    }

    @Override
    public Optional<Fragment> run(SpellSource source, TickData data) throws BlunderException {
        if (catching)
            return catchSpell.run(source, data);

        try {
            return trySpell.run(source, data);
        } catch (BlunderException blunder) {
            catching = true;
            return catchSpell.run(source, data);
        }
    }

    @Override
    public Optional<Fragment> run(SpellContext ctx) {
        return run(ctx.source(), ctx.data());
    }

    @Override
    public int getLastRunExecutions() {
        return child().getLastRunExecutions();
    }

    @Override
    public ExecutionState getDeepestState() {
        return child().getDeepestState();
    }

    private SpellExecutor child() {
        return catching ? catchSpell : trySpell;
    }
}
