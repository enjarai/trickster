package dev.enjarai.trickster.spell.fragment;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.blunder.BlunderException;

import java.util.List;

public interface AverageableFragment extends Fragment {
    AverageableFragment avg(List<AverageableFragment> other) throws BlunderException;
}
