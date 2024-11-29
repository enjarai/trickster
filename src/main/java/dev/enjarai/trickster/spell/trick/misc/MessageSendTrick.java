package dev.enjarai.trickster.spell.trick.misc;

import java.util.List;

import dev.enjarai.trickster.cca.MessageHandlerComponent.Key;
import dev.enjarai.trickster.cca.ModGlobalComponents;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.ItemInvalidBlunder;
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
        var slot = supposeInput(fragments, FragmentType.SLOT, 1);
        var key = slot.<Key>map(s -> {
            var comp = s.reference(this, ctx).get(ModComponents.MANA);

            if (comp != null && comp.pool() instanceof SharedManaPool pool) {
                return new Key.Channel(pool.uuid());
            }

            throw new ItemInvalidBlunder(this);
        }).orElse(new Key.Broadcast(ctx.source().getWorld().getRegistryKey(), ctx.source().getPos()));

        ModGlobalComponents.MESSAGE_HANDLER.get(ctx.source().getWorld().getScoreboard()).send(key, value);
        return value;
    }
}
