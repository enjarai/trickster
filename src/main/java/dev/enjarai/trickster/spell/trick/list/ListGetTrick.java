package dev.enjarai.trickster.spell.trick.list;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.IndexOutOfBoundsBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.ArgType;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class ListGetTrick extends DistortionTrick<ListGetTrick> {
    public ListGetTrick() {
        super(Pattern.of(0, 3, 6, 4, 8, 5, 2), Signature.of(ArgType.ANY.listOfArg(), FragmentType.NUMBER, ListGetTrick::run, RetType.ANY));
    }

    public Fragment run(SpellContext ctx, List<Fragment> list, NumberFragment index) throws BlunderException {
        if (index.number() < 0 || index.number() >= list.size()) {
            throw new IndexOutOfBoundsBlunder(this, MathHelper.floor(index.number()));
        }

        return list.get(MathHelper.floor(index.number()));
    }
}
