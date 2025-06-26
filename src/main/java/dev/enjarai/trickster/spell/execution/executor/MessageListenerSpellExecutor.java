package dev.enjarai.trickster.spell.execution.executor;

import java.util.List;
import java.util.Optional;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.cca.MessageHandlerComponent.Key;
import dev.enjarai.trickster.cca.ModGlobalComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellExecutor;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.execution.ExecutionState;
import dev.enjarai.trickster.spell.execution.TickData;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;

public class MessageListenerSpellExecutor implements SpellExecutor {
    public static final StructEndec<MessageListenerSpellExecutor> ENDEC = StructEndecBuilder.of(
            ExecutionState.ENDEC.fieldOf("state", e -> e.state),
            EndecTomfoolery.forcedSafeOptionalOf(
                    EndecTomfoolery.UUID.xmap(
                            Key.Channel::new,
                            Key.Channel::uuid
                    )
            ).fieldOf("channel", e -> e.channel),
            Endec.INT.optionalFieldOf("ticksLeft", e -> e.ticksLeft, 0), //TODO: is this fine???? (the optional field of)
            EndecTomfoolery.forcedSafeOptionalOf(ListFragment.ENDEC).fieldOf("result", e -> e.result),
            MessageListenerSpellExecutor::new
    );

    private final ExecutionState state; // DO NOT USE
    private final Optional<Key.Channel> channel;
    private int ticksLeft;
    private Optional<ListFragment> result = Optional.empty();

    private MessageListenerSpellExecutor(ExecutionState state, Optional<Key.Channel> channel, int ticksLeft, Optional<ListFragment> result) {
        this.state = state;
        this.channel = channel;
        this.ticksLeft = ticksLeft;
        this.result = result;
    }

    public MessageListenerSpellExecutor(ExecutionState state, Optional<Integer> timeout, Optional<Key.Channel> channel) {
        this(state, channel, timeout.map(i -> i < 0 ? i : i + 1).orElse(-1), Optional.empty());
    }

    @Override
    public SpellExecutorType<?> type() {
        return SpellExecutorType.MESSAGE_LISTENER;
    }

    @Override
    public SpellPart spell() {
        return new SpellPart();
    }

    @Override
    public Optional<Fragment> run(SpellSource source, TickData data) throws BlunderException {
        if (ticksLeft == 0) {
            return Optional.of(new ListFragment(List.of()));
        }

        if (result.isEmpty()) {
            ModGlobalComponents.MESSAGE_HANDLER
                    .get(source.getWorld().getScoreboard())
                    .await(channel.<Key>map(n -> n).orElseGet(() -> new Key.Broadcast(source.getWorld().getRegistryKey(), source.getPos(), 0)), this::listen);
        }

        if (ticksLeft > 0) {
            ticksLeft--;
        }

        return result.map(n -> n);
    }

    @Override
    public Optional<Fragment> run(SpellContext ctx) throws BlunderException {
        return run(ctx.source(), ctx.data());
    }

    @Override
    public int getLastRunExecutions() {
        return 0;
    }

    @Override
    public ExecutionState getDeepestState() {
        return state;
    }

    private void listen(ListFragment value) {
        result = Optional.of(value);
    }
}
