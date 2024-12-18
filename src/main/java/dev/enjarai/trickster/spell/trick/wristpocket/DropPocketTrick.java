package dev.enjarai.trickster.spell.trick.wristpocket;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.IncompatibleSourceBlunder;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;

import java.util.List;

public class DropPocketTrick extends Trick {
    public DropPocketTrick() {
        super(Pattern.of(7, 3, 4, 5, 7, 4, 2, 1, 0, 4));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var wristpocket = ctx.source().getComponent(ModEntityComponents.WRIST_POCKET)
                .orElseThrow(() -> new IncompatibleSourceBlunder(this));

        wristpocket.drop(ctx);
        return VoidFragment.INSTANCE;
    }
}
