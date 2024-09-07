package dev.enjarai.trickster.spell.execution.executor;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.execution.ExecutionState;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;

import java.util.List;
import java.util.Optional;

public class TryCatchSpellExecutor implements SpellExecutor {
    public static final StructEndec<TryCatchSpellExecutor> ENDEC = StructEndecBuilder.of(
            SpellExecutor.ENDEC.fieldOf("try", e -> e.trySpell),
            SpellExecutor.ENDEC.fieldOf("catch", e -> e.catchSpell),
            Endec.BOOLEAN.fieldOf("catching", e -> e.catching),
            TryCatchSpellExecutor::new
    );

    protected final SpellExecutor trySpell;
    protected final SpellExecutor catchSpell;
    protected boolean catching = false;
    protected int lastRunExecutions;

    protected TryCatchSpellExecutor(SpellExecutor trySpell, SpellExecutor catchSpell, boolean catching) {
        this.trySpell = trySpell;
        this.catchSpell = catchSpell;
        this.catching = catching;
    }

    public TryCatchSpellExecutor(SpellContext ctx, SpellPart trySpell, SpellPart catchSpell, List<Fragment> arguments) {
        this.trySpell = new DefaultSpellExecutor(trySpell, ctx.executionState().recurseOrThrow(arguments));
        this.catchSpell = new DefaultSpellExecutor(catchSpell, ctx.executionState().recurseOrThrow(arguments));
    }

    @Override
    public SpellExecutorType<?> type() {
        return SpellExecutorType.TRY_CATCH;
    }

    @Override
    public Optional<Fragment> run(SpellContext ctx, ExecutionCounter executions) {
        lastRunExecutions = 0;

        if (catching)
            return catchSpell.run(ctx.source());

        try {
            return trySpell.run(ctx.source());
        } catch (BlunderException blunder) {
            catching = true;
            catchSpell.getCurrentState().syncLinksFrom(trySpell.getCurrentState());
            return catchSpell.run(ctx.source());
        }
    }

    @Override
    public int getLastRunExecutions() {
        return child().getLastRunExecutions();
    }

    @Override
    public ExecutionState getCurrentState() {
        return child().getCurrentState();
    }

    protected SpellExecutor child() {
        return catching ? catchSpell : trySpell;
    }
}
