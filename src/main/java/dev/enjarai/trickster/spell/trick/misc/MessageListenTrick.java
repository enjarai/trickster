package dev.enjarai.trickster.spell.trick.misc;

import dev.enjarai.trickster.item.ChannelItem;
import dev.enjarai.trickster.spell.EvaluationResult;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellExecutor;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.ItemInvalidBlunder;
import dev.enjarai.trickster.spell.blunder.OutOfRangeBlunder;
import dev.enjarai.trickster.spell.execution.executor.MessageListenerSpellExecutor;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.Optional;

public class MessageListenTrick extends Trick<MessageListenTrick> {
    public MessageListenTrick() {
        super(Pattern.of(4, 0, 7, 2, 4), Signature.of(FragmentType.NUMBER.optionalOfArg(), MessageListenTrick::run, RetType.ANY.listOfRet().executor()));
        overload(Signature.of(FragmentType.NUMBER.optionalOfArg(), FragmentType.SLOT, MessageListenTrick::runWithChannel,
                RetType.ANY.listOfRet().thisFunctionExistsSolelyForMessageListeningOnItemsBecauseWeAlreadyHadAnAbstractionForItAndWeReallyDontWantToReworkItSoThisWillHaveToDoHonestly()));
    }

    //TODO: how should we stop this from running in single-tick mode
    public SpellExecutor run(SpellContext ctx, Optional<NumberFragment> timeout) throws BlunderException {
        return new MessageListenerSpellExecutor(ctx.state(), timeout.map(n -> n.asInt()), Optional.empty());
    }

    public EvaluationResult runWithChannel(SpellContext ctx, Optional<NumberFragment> timeout, SlotFragment slot) throws BlunderException {
        var stack = slot.reference(this, ctx);
        var range = ctx.source().getPos().distance(slot.getSourceOrCasterPos(this, ctx));
        var item = stack.getItem();

        if (item instanceof ChannelItem channelItem) {
            if (range > channelItem.getRange()) {
                throw new OutOfRangeBlunder(this, channelItem.getRange(), range);
            }

            return channelItem.messageListenBehavior(this, ctx, stack, timeout.map(n -> n.asInt()));
        }

        throw new ItemInvalidBlunder(this);
    }

}
