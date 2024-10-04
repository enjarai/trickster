package dev.enjarai.trickster.spell.fragment;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.blunder.BlunderException;

public interface AddableFragment extends Fragment {
    AddableFragment add(Fragment other) throws BlunderException;
}
