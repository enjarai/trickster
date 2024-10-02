package dev.enjarai.trickster.spell.trick.math;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.MissingInputsBlunder;

import java.util.List;

public class MaxTrick extends DistortionTrick {
    public MaxTrick() {
        super(Pattern.of(3, 1, 5));
    }

    @Override
    public Fragment distort(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        fragments = supposeInput(fragments, 0)
           .flatMap(l -> supposeType(l, FragmentType.LIST))
           .map(ListFragment::contents)
           .orElse(fragments);

        return new NumberFragment(fragments.stream()
                .mapToDouble(frag -> expectType(frag, FragmentType.NUMBER).number())
                .max()
                .orElseThrow(() -> new MissingInputsBlunder(this)));
    }
}
