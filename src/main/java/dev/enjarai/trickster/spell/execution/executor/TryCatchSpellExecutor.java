package dev.enjarai.trickster.spell.execution.executor;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellInstruction;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.execution.ExecutionState;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;

import java.util.List;
import java.util.Optional;

public class TryCatchSpellExecutor extends DefaultSpellExecutor {
    public static final StructEndec<TryCatchSpellExecutor> ENDEC = StructEndecBuilder.of(
            DefaultSpellExecutor.ENDEC.fieldOf("self", e -> e),
            SpellExecutor.ENDEC.fieldOf("try", e -> e.trySpell),
            SpellExecutor.ENDEC.fieldOf("catch", e -> e.catchSpell),
            Endec.BOOLEAN.fieldOf("catching", e -> e.catching),
            (self, trySpell, catchSpell, catching) -> new TryCatchSpellExecutor(
                    self.instructions, self.inputs, self.scope, self.state, self.child, self.overrideReturnValue,
                    trySpell, catchSpell, catching
            )
    );

    protected final SpellExecutor trySpell;
    protected final SpellExecutor catchSpell;
    protected boolean catching = false;

    protected TryCatchSpellExecutor(List<SpellInstruction> instructions, List<Fragment> inputs, List<Integer> scope, ExecutionState state, Optional<SpellExecutor> child, Optional<Fragment> overrideReturnValue, SpellExecutor trySpell, SpellExecutor catchSpell, boolean catching) {
        super(instructions, inputs, scope, state, child, overrideReturnValue);
        this.trySpell = trySpell;
        this.catchSpell = catchSpell;
        this.catching = catching;
    }

    public TryCatchSpellExecutor(SpellContext ctx, SpellPart trySpell, SpellPart catchSpell, List<Fragment> arguments) {
        super(new SpellPart(), List.of());
        this.state = ctx.executionState().recurseOrThrow(List.of());
        this.trySpell = new DefaultSpellExecutor(trySpell, this.state.recurseOrThrow(arguments));
        this.catchSpell = new DefaultSpellExecutor(catchSpell, this.state.recurseOrThrow(arguments));
    }

    @Override
    public SpellExecutorType<?> type() {
        return SpellExecutorType.TRY_CATCH;
    }

    @Override
    public Optional<Fragment> run(SpellContext ctx, int executions) {
        lastRunExecutions = 0;

        if (catching)
            return catchSpell.run(ctx.source());

        try {
            return trySpell.run(ctx.source());
        } catch (BlunderException blunder) {
            catching = true;
            return catchSpell.run(ctx.source());
        }
    }
}
