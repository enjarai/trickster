package dev.enjarai.trickster.spell.fragment;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.blunder.BlunderException;

public interface DivisibleFragment extends Fragment {
    DivisibleFragment divide(Fragment other) throws BlunderException;
}
