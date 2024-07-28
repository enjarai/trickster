package dev.enjarai.trickster.spell.trick.list;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.blunder.IndexOutOfBoundsBlunder;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class ListGetTrick extends Trick {
    public ListGetTrick() {
        super(Pattern.of(0, 3, 6, 4, 8, 5, 2));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var list = expectInput(fragments, FragmentType.LIST, 0);
        var index = expectInput(fragments, FragmentType.NUMBER, 1);

        if (index.number() < 0 || index.number() >= list.fragments().size()) {
            throw new IndexOutOfBoundsBlunder(this, MathHelper.floor(index.number()));
        }

        return list.fragments().get(MathHelper.floor(index.number()));
    }
}
