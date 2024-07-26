package dev.enjarai.trickster.spell.trick.block;

import dev.enjarai.trickster.block.SpellControlledRedstoneBlock;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class PowerResonatorTrick extends Trick {
    public PowerResonatorTrick() {
        super(Pattern.of(7, 8, 6, 7, 2, 1, 0, 7));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var pos = expectInput(fragments, FragmentType.VECTOR, 0);
        var power = expectInput(fragments, FragmentType.NUMBER, 1);
        var blockPos = pos.toBlockPos();
        var intPower = MathHelper.clamp((int) power.number(), 0, 15);
        var world = ctx.source().getWorld();
        expectCanBuild(ctx, blockPos);

        if (world.getBlockState(blockPos).getBlock() instanceof SpellControlledRedstoneBlock block) {
            ctx.useMana(this, (float) Math.sqrt(ctx.source().getBlockPos().getSquaredDistance(blockPos)) / 2f);
            var result = block.setPower(world, blockPos, intPower);

            if (result) {
//                var particlePos = blockPos.toCenterPos();
//                world.spawnParticles(
//                        ModParticles.PROTECTED_BLOCK, particlePos.x, particlePos.y, particlePos.z,
//                        1, 0, 0, 0, 0
//                );
                world.playSound(
                        null, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5,
                        SoundEvents.BLOCK_BELL_USE, SoundCategory.BLOCKS, 1, 1.6f + 0.1f / 3 * intPower, 0
                );

                return BooleanFragment.TRUE;
            }

            return BooleanFragment.FALSE;
        }

        throw new BlockInvalidBlunder(this);
    }
}
