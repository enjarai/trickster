package dev.enjarai.trickster.spell.trick.misc;

import java.util.Optional;

import dev.enjarai.trickster.cca.MessageHandlerComponent.Key;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.ItemInvalidBlunder;
import dev.enjarai.trickster.spell.blunder.OutOfRangeBlunder;
import dev.enjarai.trickster.spell.execution.executor.MessageListenerSpellExecutor;
import dev.enjarai.trickster.spell.SpellExecutor;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.mana.SharedManaPool;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

public class MessageListenTrick extends Trick<MessageListenTrick> {
    public MessageListenTrick() {
        super(Pattern.of(4, 0, 7, 2, 4), Signature.of(FragmentType.NUMBER, FragmentType.SLOT.optionalOf(), MessageListenTrick::run));
    }

    //TODO: how should we stop this from running in single-tick mode
    public SpellExecutor run(SpellContext ctx, NumberFragment timeout, Optional<SlotFragment> slot) throws BlunderException {
        var channel = slot.map(s -> {
            var range = s.getSourcePos(this, ctx).toCenterPos().subtract(ctx.source().getBlockPos().toCenterPos()).length();

            if (range > 16) {
                throw new OutOfRangeBlunder(this, 16.0, range);
            }

            var comp = s.reference(this, ctx).get(ModComponents.MANA);

            if (comp != null && comp.pool() instanceof SharedManaPool pool) {
                return new Key.Channel(pool.uuid());
            }

            throw new ItemInvalidBlunder(this);
        });

        return new MessageListenerSpellExecutor(ctx.state(), timeout.asInt(), channel);
    }
}
