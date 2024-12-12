package dev.enjarai.trickster.spell.trick.misc;

import java.util.List;

import com.mojang.datafixers.util.Either;

import dev.enjarai.trickster.cca.MessageHandlerComponent.Key;
import dev.enjarai.trickster.cca.ModGlobalComponents;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.ItemInvalidBlunder;
import dev.enjarai.trickster.spell.blunder.OutOfRangeBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.mana.SharedManaPool;
import dev.enjarai.trickster.spell.trick.Trick;

public class MessageSendTrick extends Trick {
    public MessageSendTrick() {
        super(Pattern.of(4, 8, 1, 6, 4));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var value = expectInput(fragments, 0);
        var secondary = supposeEitherInput(fragments, FragmentType.NUMBER, FragmentType.SLOT, 1);
        var key = secondary.map(either -> either
                .<Key>mapLeft(n -> {
                    ctx.useMana(this, (float) n.number());
                    return new Key.Broadcast(ctx.source().getWorld().getRegistryKey(), ctx.source().getPos(), n.number());
                })
                .<Key>mapRight(s -> {
                    var range = s.getSourcePos(this, ctx).toCenterPos().subtract(ctx.source().getBlockPos().toCenterPos()).length();

                    if (range > 16) {
                        throw new OutOfRangeBlunder(this, 16.0, range);
                    }

                    var comp = s.reference(this, ctx).get(ModComponents.MANA);

                    if (comp != null && comp.pool() instanceof SharedManaPool pool) {
                        return new Key.Channel(pool.uuid());
                    }

                    throw new ItemInvalidBlunder(this);
                }))
            .map(Either::unwrap)
            .orElseGet(() -> new Key.Broadcast(ctx.source().getWorld().getRegistryKey(), ctx.source().getPos(), 0));

        ModGlobalComponents.MESSAGE_HANDLER.get(ctx.source().getWorld().getScoreboard()).send(key, value);
        return value;
    }
}
