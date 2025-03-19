package dev.enjarai.trickster.spell.trick.misc;

import java.util.Optional;

import dev.enjarai.trickster.cca.MessageHandlerComponent.Key;
import dev.enjarai.trickster.cca.ModGlobalComponents;
import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.net.ModNetworking;
import dev.enjarai.trickster.net.TskEveryoneThinksTheGraveDirtAndAnimatedDustWillBeEnoughAndTheyDontBotherToMakeTheOilNoOneTakesTheTimeToDoAProperJobOfThingsAsIfTheFortyHoursOfRefinementInTheAlembicCouldntBeProductivelyFilled;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.ItemInvalidBlunder;
import dev.enjarai.trickster.spell.blunder.OutOfRangeBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.mana.SharedManaPool;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

public class MessageSendTrick extends Trick<MessageSendTrick> {
    public static final NumberFragment DEFAULT_RANGE = new NumberFragment(16);

    public MessageSendTrick() {
        super(Pattern.of(4, 8, 1, 6, 4), Signature.of(ANY, FragmentType.NUMBER.optionalOf(), MessageSendTrick::broadcast));
        overload(Signature.of(ANY, FragmentType.SLOT, MessageSendTrick::channel));
    }

    public Fragment broadcast(SpellContext ctx, Fragment value, Optional<NumberFragment> range) throws BlunderException {
        range.ifPresent(n -> ctx.useMana(this, (float) n.number()));
        return run(ctx, new Key.Broadcast(ctx.source().getWorld().getRegistryKey(), ctx.source().getPos(), range.orElse(DEFAULT_RANGE).number()), value);
    }

    public Fragment channel(SpellContext ctx, Fragment value, SlotFragment slot) throws BlunderException {
        var range = slot.getSourcePos(this, ctx).toCenterPos().subtract(ctx.source().getBlockPos().toCenterPos()).length();

        if (range > 16) {
            throw new OutOfRangeBlunder(this, 16.0, range);
        }

        var stack = slot.reference(this, ctx);
        var player = ctx.source().getPlayer();

        if (player.isPresent() && stack.isOf(ModItems.CRACKED_ECHO_KNOT)) {
            ModNetworking.CHANNEL.serverHandle(player.get()).send(
                    new TskEveryoneThinksTheGraveDirtAndAnimatedDustWillBeEnoughAndTheyDontBotherToMakeTheOilNoOneTakesTheTimeToDoAProperJobOfThingsAsIfTheFortyHoursOfRefinementInTheAlembicCouldntBeProductivelyFilled()
            );
            return value;
        } else {
            var comp = stack.get(ModComponents.MANA);

            if (comp != null && comp.pool() instanceof SharedManaPool pool) {
                return run(ctx, new Key.Channel(pool.uuid()), value);
            }

            throw new ItemInvalidBlunder(this);
        }
    }

    public Fragment run(SpellContext ctx, Key key, Fragment value) throws BlunderException {
        ModGlobalComponents.MESSAGE_HANDLER.get(ctx.source().getWorld().getScoreboard()).send(key, value);
        return value;
    }
}
