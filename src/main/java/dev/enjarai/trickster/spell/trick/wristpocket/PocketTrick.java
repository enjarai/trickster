package dev.enjarai.trickster.spell.trick.wristpocket;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.IncompatibleSourceBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;

import java.util.List;

public class PocketTrick extends Trick {

    public PocketTrick() {
        super(Pattern.of(1,5,4,3,1,4,8,7,6,4));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var wristpocket = ctx.source().getComponent(ModEntityComponents.WRIST_POCKET)
                .orElseThrow(() -> new IncompatibleSourceBlunder(this));

        var slot = expectInput(fragments, FragmentType.SLOT, 0);
        var item = slot.getItem(this, ctx);
        var count = supposeInput(fragments, FragmentType.NUMBER, 1).map(NumberFragment::asInt).orElse(item.getMaxCount());

        var moved = slot.move(this, ctx, count);

        wristpocket.put(moved, ctx);

       return VoidFragment.INSTANCE;
    }
}
