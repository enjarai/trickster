package dev.enjarai.trickster.spell.trick.mana;

import dev.enjarai.trickster.particle.ModParticles;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.block.Blocks;

public class DrainMatterTrick extends Trick<DrainMatterTrick> {
    public DrainMatterTrick() {
        super(Pattern.of());
    }

    public Fragment run(SpellContext ctx, VectorFragment pos) throws BlunderException {
        var world = ctx.source().getWorld();
        float amount = 0; //TODO: get the value

        var particlePos = pos.toBlockPos().toCenterPos();
        world.spawnParticles(
                ModParticles.PROTECTED_BLOCK, particlePos.x, particlePos.y, particlePos.z,
                1, 0, 0, 0, 0);
        world.setBlockState(pos.toBlockPos(), Blocks.AIR.getDefaultState());
        return new NumberFragment(ctx.source().getManaPool().refill(amount, world));
    }
}
