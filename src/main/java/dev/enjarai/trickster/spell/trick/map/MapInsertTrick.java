package dev.enjarai.trickster.spell.trick.map;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.Map.Hamt;
import dev.enjarai.trickster.spell.fragment.Map.MapFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.blunder.IncorrectFragmentBlunder;
import dev.enjarai.trickster.spell.trick.blunder.MissingFragmentBlunder;
import dev.enjarai.trickster.spell.trick.blunder.MissingInputsBlunder;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MapInsertTrick extends Trick {
    public MapInsertTrick() {
        super(Pattern.of(0, 3, 6, 8, 5, 2, 4, 8));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        Iterator<Fragment> fragmentIterator = fragments.iterator();

        if (!fragmentIterator.hasNext()) {
            throw new MissingFragmentBlunder(this, 0, FragmentType.MAP.getName());
        }
        var first = expectType(fragmentIterator.next(), FragmentType.MAP);

        var map = ((MapFragment) first).map();
        int index = 1;
        while(fragmentIterator.hasNext()) {
            Fragment key = fragmentIterator.next();
            if (fragmentIterator.hasNext()) {
                Fragment value = fragmentIterator.next();
                map = map.assoc(key, value);
            } else {
                throw new MissingFragmentBlunder(this, index, Text.of("any"));
            }
            index++;
        }

        return new MapFragment(map);
    }
}
