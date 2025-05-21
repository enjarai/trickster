package dev.enjarai.trickster.spell.trick.misc;

import java.util.Optional;

import dev.enjarai.trickster.item.ChannelItem;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellExecutor;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.ItemInvalidBlunder;
import dev.enjarai.trickster.spell.blunder.OutOfRangeBlunder;
import dev.enjarai.trickster.spell.EvaluationResult;
import dev.enjarai.trickster.spell.execution.executor.MessageListenerSpellExecutor;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

public class MessageListenTrick extends Trick<MessageListenTrick> {
    public MessageListenTrick() {
        super(Pattern.of(4, 0, 7, 2, 4), Signature.of(FragmentType.NUMBER, MessageListenTrick::run));
        overload(Signature.of(FragmentType.NUMBER, FragmentType.SLOT, MessageListenTrick::runWithChannel));
    }

    //TODO: how should we stop this from running in single-tick mode
    public SpellExecutor run(SpellContext ctx, NumberFragment timeout) throws BlunderException {
        return new MessageListenerSpellExecutor(ctx.state(), timeout.asInt(), Optional.empty());
    }

    public EvaluationResult runWithChannel(SpellContext ctx, NumberFragment timeout, SlotFragment slot) throws BlunderException {
        var stack = slot.reference(this, ctx);
        var range = ctx.source().getPos().distance(slot.getSourcePos(this, ctx));
        var item = stack.getItem();

        if (item instanceof ChannelItem channelItem) {
            if (range > channelItem.getRange()) {
                throw new OutOfRangeBlunder(this, channelItem.getRange(), range);
            }

            return channelItem.messageListenBehavior(this, ctx, stack, timeout.asInt());
        }

        throw new ItemInvalidBlunder(this);
    }

}
