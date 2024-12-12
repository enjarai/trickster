package dev.enjarai.trickster.spell.fragment;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.execution.executor.FoldingSpellExecutor;

public interface FoldableFragment extends Fragment {
    FoldingSpellExecutor fold(SpellContext ctx, SpellPart executable, Fragment identity);
}
