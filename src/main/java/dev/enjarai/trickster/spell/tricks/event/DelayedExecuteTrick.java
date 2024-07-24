package dev.enjarai.trickster.spell.tricks.event;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class DelayedExecuteTrick extends Trick {
    public static Queue<Runnable> QUEUE = new ArrayDeque<>();

    public DelayedExecuteTrick() {
        super(Pattern.of(3, 4, 5, 8, 7, 6, 3, 0, 1, 4, 7, 2, 3));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var spell = expectInput(fragments, FragmentType.SPELL_PART, 0).deepClone();

        QUEUE.add(() -> spell.runSafely(ctx.delayed(fragments.subList(1, fragments.size()))));
        return VoidFragment.INSTANCE;
    }
}
