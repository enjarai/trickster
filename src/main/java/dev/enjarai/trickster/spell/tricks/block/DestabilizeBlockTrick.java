package dev.enjarai.trickster.spell.tricks.block;

import dev.enjarai.trickster.particle.ModParticles;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlockUnoccupiedBlunder;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.OverlapBlunder;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.List;

public class DestabilizeBlockTrick extends Trick {
    public DestabilizeBlockTrick() {
        super(Pattern.of(4, 3, 0, 2, 5, 4, 7));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var pos = expectInput(fragments, FragmentType.VECTOR, 0);
        var blockPos = pos.toBlockPos();

        expectCanBuild(ctx, blockPos);

        var state = ctx.getWorld().getBlockState(blockPos);

        if (state.isAir()) {
            throw new BlockUnoccupiedBlunder(this, pos);
        }

        FallingBlockEntity.spawnFromBlock(ctx.getWorld(), blockPos, state);

        var particlePos = blockPos.toCenterPos();
        ctx.getWorld().spawnParticles(
                ModParticles.PROTECTED_BLOCK, particlePos.x, particlePos.y, particlePos.z,
                1, 0, 0, 0, 0
        );

        return VoidFragment.INSTANCE;
    }
}
