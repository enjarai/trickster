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
import java.util.Objects;

public class ListRemoveTrick extends DistortionTrick<ListRemoveTrick> {
    public ListRemoveTrick() {
        super(Pattern.of(6, 3, 0, 4, 8, 5, 2), Signature.of(ArgType.ANY.listOfArg(), FragmentType.NUMBER.variadicOfArg().unpack(), ListRemoveTrick::remove, RetType.ANY.listOfRet()));
    }

    public List<Fragment> remove(SpellContext ctx, List<Fragment> list, List<NumberFragment> indexes) {
        for (var index : indexes) {
            if (index.number() < 0 || index.number() >= list.size()) {
                throw new IndexOutOfBoundsBlunder(this, MathHelper.floor(index.number()));
            }
        }

        var newList = new ArrayList<Fragment>(list.size());
        newList.addAll(list);

        for (var index : indexes) {
            newList.set(MathHelper.floor(index.number()), null);
        }
        newList.removeIf(Objects::isNull);

        return ImmutableList.copyOf(newList);
    }
}
