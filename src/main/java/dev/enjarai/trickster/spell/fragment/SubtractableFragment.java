package dev.enjarai.trickster.spell.fragment;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;

public interface SubtractableFragment extends Fragment {
    SubtractableFragment subtract(Fragment other) throws BlunderException;
}
