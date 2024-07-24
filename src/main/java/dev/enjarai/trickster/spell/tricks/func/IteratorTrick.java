package dev.enjarai.trickster.spell.tricks.func;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;

import java.util.ArrayList;
import java.util.List;

public class IteratorTrick extends Trick {
    public IteratorTrick() {
        super(Pattern.of(3, 6, 4, 0, 1, 2, 5, 8, 7, 4, 3));
    }

    @Override
    public Fragment activate(SpellSource ctx, List<Fragment> fragments) throws BlunderException {
        var executable = expectInput(fragments, FragmentType.SPELL_PART, 0);
        var list = expectInput(fragments, FragmentType.LIST, 1);
        var result = new ArrayList<Fragment>();
        int index = 0;

        for (var item : list.fragments()) {
            var args = new ArrayList<Fragment>();
            args.add(item);
            args.add(new NumberFragment(index));
            args.add(list);
            ctx.pushPartGlyph(args);
            ctx.pushStackTrace(-2);
            result.add(executable.run(ctx));
            ctx.popStackTrace();
            ctx.popPartGlyph();
            index++;
        }

        return new ListFragment(result);
    }
}
