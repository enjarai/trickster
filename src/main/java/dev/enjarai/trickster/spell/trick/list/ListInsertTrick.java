package dev.enjarai.trickster.spell.trick.list;

import com.google.common.collect.ImmutableList;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.IndexOutOfBoundsBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.ArgType;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class ListInsertTrick extends DistortionTrick<ListInsertTrick> {
    public ListInsertTrick() {
        super(Pattern.of(6, 3, 0, 4, 2, 5, 8), Signature.of(ArgType.ANY.listOfArg(), FragmentType.NUMBER, ArgType.ANY.variadicOfArg(), ListInsertTrick::run, RetType.ANY.listOfRet()));
    }

    public List<Fragment> run(SpellContext ctx, List<Fragment> list, NumberFragment index, List<Fragment> toAdd) {
        if (index.number() < 0 || index.number() > list.size()) {
            throw new IndexOutOfBoundsBlunder(this, MathHelper.floor(index.number()));
        }

        var newList = new ArrayList<Fragment>(list.size() + 1);
        newList.addAll(list);
        newList.addAll((int) Math.floor(index.number()), toAdd);
        return ImmutableList.copyOf(newList);
    }
}
