package dev.enjarai.trickster.spell.tricks.tree;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.AddressNotInTreeBlunder;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.IncorrectFragmentBlunder;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RemoveSubtreeTrick extends Trick {
    public RemoveSubtreeTrick() {
        super(Pattern.of(0, 3, 6, 4, 2, 5, 8));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var spell = expectInput(fragments, SpellPart.class, 0);
        var addressFragment = expectInput(fragments, ListFragment.class, 1);

        var address = sanitizeAddress(addressFragment);
        var newSpell = spell.deepClone();

        SpellPart prev = null;
        var node = newSpell;
        for (int index : address) {
            var subParts = node.subParts;
            if (subParts.size() > index && subParts.get(index).isPresent()) {
                var newNode = subParts.get(index).get();
                prev = node;
                node = newNode;
            } else {
                throw new AddressNotInTreeBlunder(this, address);
            }
        }
        if (prev == null) {
            return VoidFragment.INSTANCE;
        } else {
            prev.subParts.remove(address.getLast().intValue());
            return newSpell;
        }
    }

    private List<Integer> sanitizeAddress(ListFragment address) {
        var sanitizedAddress = new ArrayList<Integer>();

        for (Fragment fragment : address.fragments()) {
            if (fragment instanceof NumberFragment index && index.isInteger()) {
                sanitizedAddress.add((int) index.number());
            } else {
                throw new IncorrectFragmentBlunder(
                        this,
                        1,
                        Text.translatable(Trickster.MOD_ID + ".fragment." + Trickster.MOD_ID + "." + "integer_list"),
                        address);
            }
        }

        return sanitizedAddress;
    }
}
