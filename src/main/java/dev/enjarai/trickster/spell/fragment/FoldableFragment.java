package dev.enjarai.trickster.spell.fragment;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.execution.executor.SpellExecutor;

public interface FoldableFragment extends Fragment {
    SpellExecutor fold(SpellContext ctx, SpellPart executable, Fragment identity);
}
