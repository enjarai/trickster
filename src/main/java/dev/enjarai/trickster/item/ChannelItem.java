package dev.enjarai.trickster.item;

import dev.enjarai.trickster.spell.EvaluationResult;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.item.ItemStack;

public interface ChannelItem {
    EvaluationResult messageListenBehavior(Trick<?> trickSource, SpellContext ctx, ItemStack stack, int timeout);

    void messageSendBehavior(Trick<?> trickSource, SpellContext ctx, ItemStack stack, Fragment value);

    int getRange();
}
