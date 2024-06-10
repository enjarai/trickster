package dev.enjarai.trickster.spell.fragment;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;

public interface MultiplicableFragment extends Fragment {
    MultiplicableFragment multiply(Fragment other) throws BlunderException;
}
