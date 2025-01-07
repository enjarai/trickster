package dev.enjarai.trickster.spell.trick.basic;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.NoPlayerBlunder;
import dev.enjarai.trickster.spell.blunder.OutOfRangeBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.StringFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;

import java.util.List;

public class WriteSpellTrick extends Trick {
    public WriteSpellTrick() {
        super(Pattern.of(1, 4, 7, 8, 5, 4, 3, 6, 7));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return activate(ctx, fragments, false);
    }

    public Fragment activate(SpellContext ctx, List<Fragment> fragments, boolean closed) throws BlunderException {
        var player = ctx.source().getPlayer();
        var input = expectInput(fragments, 0).applyEphemeral();
        var slot = supposeInput(fragments, FragmentType.SLOT, 1).or(() -> ctx.source().getOtherHandSlot())
                .orElseThrow(() -> new NoPlayerBlunder(this));
        var name = supposeInput(fragments, FragmentType.STRING, 2).map(StringFragment::asText);
        var range = slot.getSourcePos(this, ctx).toCenterPos().subtract(ctx.source().getBlockPos().toCenterPos())
                .length();

        if (range > 16) {
            throw new OutOfRangeBlunder(this, 16.0, range);
        }

        slot.writeFragment(input, closed, name, player, this, ctx);
        return input;
    }
}
