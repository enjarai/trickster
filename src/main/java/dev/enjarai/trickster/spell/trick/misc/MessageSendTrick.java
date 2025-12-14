package dev.enjarai.trickster.spell.trick.misc;

import dev.enjarai.trickster.cca.MessageHandlerComponent.Key;
import dev.enjarai.trickster.cca.ModGlobalComponents;
import dev.enjarai.trickster.item.ChannelItem;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.ItemInvalidBlunder;
import dev.enjarai.trickster.spell.blunder.OutOfRangeBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.slot.SlotFragment;
import dev.enjarai.trickster.spell.fragment.slot.VariantType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.ArgType;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.Optional;

public class MessageSendTrick extends Trick<MessageSendTrick> {
    public static final NumberFragment DEFAULT_RANGE = new NumberFragment(16);

    public MessageSendTrick() {
        super(Pattern.of(4, 8, 1, 6, 4), Signature.of(ArgType.ANY, FragmentType.NUMBER.optionalOfArg(), MessageSendTrick::broadcast, RetType.ANY));
        overload(Signature.of(ArgType.ANY, FragmentType.SLOT, MessageSendTrick::channel, RetType.ANY));
    }

    public Fragment broadcast(SpellContext ctx, Fragment value, Optional<NumberFragment> range) {
        range.ifPresent(n -> ctx.useMana(this, (float) n.number()));
        return run(ctx, new Key.Broadcast(ctx.source().getWorld().getRegistryKey(), ctx.source().getPos(), range.orElse(DEFAULT_RANGE).number()), value);
    }

    public Fragment channel(SpellContext ctx, Fragment value, SlotFragment slot) {
        var resource = slot.getResource(this, ctx, VariantType.ITEM);
        var range = ctx.source().getPos().distance(slot.getSourceOrCasterPos(this, ctx));
        var item = resource.getItem();

        if (item instanceof ChannelItem channelItem) {
            if (range > channelItem.getRange()) {
                throw new OutOfRangeBlunder(this, channelItem.getRange(), range);
            }

            slot.applyItemModifier(this, ctx, stack -> {
                channelItem.messageSendBehavior(this, ctx, stack, value);
                return stack;
            });
            return value;
        }

        throw new ItemInvalidBlunder(this);
    }

    public Fragment run(SpellContext ctx, Key key, Fragment value) {
        ModGlobalComponents.MESSAGE_HANDLER.get(ctx.source().getWorld().getScoreboard()).send(key, value);
        return value;
    }
}
