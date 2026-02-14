package dev.enjarai.trickster.spell.fragment;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.InvalidInputsBlunder;
import dev.enjarai.trickster.spell.trick.Tricks;

import java.util.List;

public interface GenericAverageableFragment extends AddableFragment, DivisibleFragment, AverageableFragment {
    @SuppressWarnings("unchecked")
    @Override
    default AverageableFragment avg(List<AverageableFragment> other) throws BlunderException {
        var result = this;
        for (AverageableFragment fragment : other) {
            if (result.add(fragment) instanceof GenericAverageableFragment res) {
                result = res;
            } else {
                other.addFirst(this);
                // TODO to be un-hardcoded with the auri blunder rework™
                throw new InvalidInputsBlunder(Tricks.AVG, (List<Fragment>) (Object) other);
            }
        }
        if (result.divide(new NumberFragment(other.size() + 1)) instanceof AverageableFragment average) {
            return average;
        }
        other.addFirst(this);
        // TODO to be un-hardcoded with the auri blunder rework™
        throw new InvalidInputsBlunder(Tricks.AVG, (List<Fragment>) (Object) other);
    }
}
