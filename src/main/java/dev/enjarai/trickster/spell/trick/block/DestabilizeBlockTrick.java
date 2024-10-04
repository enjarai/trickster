package dev.enjarai.trickster.spell.trick.block;

import dev.enjarai.trickster.particle.ModParticles;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlockInvalidBlunder;
import dev.enjarai.trickster.spell.blunder.BlockUnoccupiedBlunder;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import net.minecraft.entity.FallingBlockEntity;

import java.util.List;

public class DestabilizeBlockTrick extends Trick {
    public DestabilizeBlockTrick() {
        super(Pattern.of(4, 3, 0, 2, 5, 4, 7));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var pos = expectInput(fragments, FragmentType.VECTOR, 0);
        var blockPos = pos.toBlockPos();
        var world = ctx.source().getWorld();
        expectCanBuild(ctx, blockPos);

        var state = world.getBlockState(blockPos);

        if (state.isAir()) {
            throw new BlockUnoccupiedBlunder(this, pos);
        }

        var hardness = state.getHardness(world, blockPos);
        if (hardness < 0 || hardness > 55.5f) {
            throw new BlockInvalidBlunder(this);
        }

        ctx.useMana(this, 10);
        var entity = FallingBlockEntity.spawnFromBlock(world, blockPos, state);

        var particlePos = blockPos.toCenterPos();
        world.spawnParticles(
                ModParticles.PROTECTED_BLOCK, particlePos.x, particlePos.y, particlePos.z,
                1, 0, 0, 0, 0
        );

        return EntityFragment.from(entity);
    }
}
