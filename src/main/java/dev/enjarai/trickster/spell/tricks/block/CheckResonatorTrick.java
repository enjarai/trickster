package dev.enjarai.trickster.spell.tricks.block;

import dev.enjarai.trickster.block.SpellControlledRedstoneBlock;
import dev.enjarai.trickster.particle.ModParticles;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlockInvalidBlunder;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class CheckResonatorTrick extends Trick {
    public CheckResonatorTrick() {
        super(Pattern.of(7, 8, 6, 7, 2, 1, 0, 7, 4));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var pos = expectInput(fragments, FragmentType.VECTOR, 0);

        var blockPos = pos.toBlockPos();

        if (ctx.getWorld().getBlockState(blockPos).getBlock() instanceof SpellControlledRedstoneBlock block) {
            return new NumberFragment(block.getPower(ctx.getWorld(), blockPos));
        }

        throw new BlockInvalidBlunder(this);
    }
}
