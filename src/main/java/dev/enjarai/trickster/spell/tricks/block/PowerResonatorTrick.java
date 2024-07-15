package dev.enjarai.trickster.spell.tricks.block;

import dev.enjarai.trickster.block.SpellControlledRedstoneBlock;
import dev.enjarai.trickster.particle.ModParticles;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.*;
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
        expectCanBuild(ctx, blockPos);
        var intPower = MathHelper.clamp((int) power.number(), 0, 15);

        if (ctx.getWorld().getBlockState(blockPos).getBlock() instanceof SpellControlledRedstoneBlock block) {
            ctx.useMana(this, 5);
            var result = block.setPower(ctx.getWorld(), blockPos, intPower);
            ctx.setWorldAffected();

            if (result) {
                var particlePos = blockPos.toCenterPos();
                ctx.getWorld().spawnParticles(
                        ModParticles.PROTECTED_BLOCK, particlePos.x, particlePos.y, particlePos.z,
                        1, 0, 0, 0, 0
                );

                return BooleanFragment.TRUE;
            }

            return BooleanFragment.FALSE;
        }

        throw new BlockInvalidBlunder(this);
    }
}
