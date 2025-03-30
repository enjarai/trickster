package dev.enjarai.trickster.spell.trick.block;

import dev.enjarai.trickster.block.ModBlocks;
import dev.enjarai.trickster.particle.ModParticles;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlockOccupiedBlunder;
import dev.enjarai.trickster.spell.blunder.BlockUnoccupiedBlunder;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.Direction;

import java.util.List;

public class ConjureFlowerTrick extends Trick<ConjureFlowerTrick> {
    public ConjureFlowerTrick() {
        super(Pattern.of(4, 0, 1, 4, 2, 5, 4, 8, 7, 4, 6, 3, 4), Signature.of(FragmentType.VECTOR, ConjureFlowerTrick::conjure));
    }

    public Fragment conjure(SpellContext ctx, VectorFragment pos) throws BlunderException {
        var blockPos = pos.toBlockPos();
        var world = ctx.source().getWorld();
        expectCanBuild(ctx, blockPos);

        if (!world.getBlockState(blockPos).isAir()) {
            throw new BlockOccupiedBlunder(this, pos);
        }
        if (!world.getBlockState(blockPos.down()).isSideSolidFullSquare(world, blockPos.down(), Direction.UP)) {
            throw new BlockUnoccupiedBlunder(this, VectorFragment.of(blockPos.down()));
        }

        var flowerType = Registries.BLOCK.getRandomEntry(ModBlocks.CONJURABLE_FLOWERS, world.getOrCreateRandom(TRICK_RANDOM));
        ctx.useMana(this, 5);
        flowerType.ifPresent(flower -> {
            world.setBlockState(blockPos, flower.value().getDefaultState());
        });

        var particlePos = blockPos.toCenterPos();
        world.spawnParticles(
                ModParticles.PROTECTED_BLOCK, particlePos.x, particlePos.y, particlePos.z,
                1, 0, 0, 0, 0
        );

        return pos;
    }
}
