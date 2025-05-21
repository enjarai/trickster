package dev.enjarai.trickster.spell.trick.dimension;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.world.World;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.IncompatibleDimensionBlunder;

public class GetMoonPhaseTrick extends Trick<GetMoonPhaseTrick> {
    public GetMoonPhaseTrick() {
        super(Pattern.of(0, 1, 4, 7, 6, 3, 0), Signature.of(GetMoonPhaseTrick::run));
    }

    public Fragment run(SpellContext ctx) throws BlunderException {
        if (ctx.source().getWorld().getRegistryKey() != World.OVERWORLD)
            throw new IncompatibleDimensionBlunder(this);
        return new NumberFragment(ctx.source().getWorld().getMoonPhase());
    }
}
