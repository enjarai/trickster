package dev.enjarai.trickster.spell.trick.tree;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.type.Signature;
import oshi.util.tuples.Pair;

import java.util.*;

public class LocateGlyphsTrick extends AbstractMetaTrick<LocateGlyphsTrick> {
    public LocateGlyphsTrick() {
        super(Pattern.of(6, 7, 8, 2, 1, 0, 4, 8, 5, 2), Signature.of(FragmentType.SPELL_PART, ANY, LocateGlyphsTrick::locate));
    }

    public Fragment locate(SpellContext ctx, SpellPart spell, Fragment glyph) throws BlunderException {
        var addresses = new ArrayList<List<Integer>>();
        search(spell, glyph, addresses);


        return new ListFragment(addresses.stream()
                .map(address -> (Fragment) new ListFragment(address.stream().map(num -> (Fragment) new NumberFragment(num)).toList())).toList());

    }

    private void search(SpellPart spell, Fragment target, List<List<Integer>> addresses) {
        Queue<Pair<Integer[], SpellPart>> queue = new LinkedList<>();

        queue.add(new Pair<>(new Integer[]{}, spell));
        while (!queue.isEmpty()) {

            var temp = queue.poll();

            if (temp.getB().glyph.equals(target))
                addresses.add(Arrays.asList(temp.getA()));

            var subParts = temp.getB().subParts;
            for (int i = 0; i < subParts.size(); i++) {
                var newAddress = Arrays.copyOfRange(temp.getA(), 0, temp.getA().length + 1);
                newAddress[temp.getA().length] = i;

                queue.add(new Pair<>(newAddress, subParts.get(i)));
            }
        }
    }
}
