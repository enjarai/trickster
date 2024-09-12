package dev.enjarai.trickster.spell.trick.basic;

import dev.enjarai.trickster.particle.ModParticles;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;

import java.util.List;

public class FlourishTrick extends Trick {
    public FlourishTrick() {super(
            Pattern.of(2,0,1,2,3,5,8,7,6,3)
    );}


    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var input = expectInput(fragments, FragmentType.VECTOR, 0);
        var block = input.toBlockPos().toCenterPos();

        ctx.source().getWorld().spawnParticles(
                ModParticles.PROTECTED_BLOCK, block.x, block.y, block.z,
                1, 0, 0, 0, 0
        );

        return input;
    }
}