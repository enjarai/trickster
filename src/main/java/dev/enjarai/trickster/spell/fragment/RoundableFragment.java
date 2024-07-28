package dev.enjarai.trickster.spell.fragment;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;

public interface RoundableFragment extends Fragment {
    RoundableFragment floor() throws BlunderException;

    RoundableFragment ceil() throws BlunderException;

    RoundableFragment round() throws BlunderException;
}
