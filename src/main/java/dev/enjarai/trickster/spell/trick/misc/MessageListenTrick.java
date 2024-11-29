package dev.enjarai.trickster.spell.trick.misc;

import java.util.List;

import dev.enjarai.trickster.cca.MessageHandlerComponent.Key;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.ExecutionLimitReachedBlunder;
import dev.enjarai.trickster.spell.blunder.ItemInvalidBlunder;
import dev.enjarai.trickster.spell.execution.executor.MessageListenerSpellExecutor;
import dev.enjarai.trickster.spell.execution.executor.SpellExecutor;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.mana.SharedManaPool;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.func.ForkingTrick;

public class MessageListenTrick extends Trick implements ForkingTrick {
    public MessageListenTrick() {
        super(Pattern.of(4, 0, 7, 2, 4));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        throw new ExecutionLimitReachedBlunder(); // the spell executor always moves to the next tick
    }

    @Override
    public SpellExecutor makeFork(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var channel = supposeInput(fragments, FragmentType.SLOT, 0).map(s -> {
            var comp = s.reference(this, ctx).get(ModComponents.MANA);

            if (comp != null && comp.pool() instanceof SharedManaPool pool) {
                return new Key.Channel(pool.uuid());
            }

            throw new ItemInvalidBlunder(this);
        });

        return new MessageListenerSpellExecutor(ctx.state(), channel);
    }
}
