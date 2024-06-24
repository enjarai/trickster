package dev.enjarai.trickster.spell.tricks.block;

import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.particle.ModParticles;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlockOccupiedBlunder;
import dev.enjarai.trickster.spell.tricks.blunder.BlockUnoccupiedBlunder;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.Direction;

import java.util.List;

public class ConjureFlowerTrick extends Trick {
    public ConjureFlowerTrick() {
        super(Pattern.of(4, 0, 1, 4, 2, 5, 4, 8, 7, 4, 6, 3, 4));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var pos = expectInput(fragments, FragmentType.VECTOR, 0);

        var blockPos = pos.toBlockPos();

        expectCanBuild(ctx, blockPos);
        if (!ctx.getWorld().getBlockState(blockPos).isAir()) {
            throw new BlockOccupiedBlunder(this);
        }
        if (!ctx.getWorld().getBlockState(blockPos.down()).isSideSolidFullSquare(ctx.getWorld(), blockPos.down(), Direction.UP)) {
            throw new BlockUnoccupiedBlunder(this, VectorFragment.of(blockPos.down()));
        }

        var flowerType = Registries.BLOCK.getRandomEntry(ModItems.CONJURABLE_FLOWERS, ctx.getWorld().getOrCreateRandom(TRICK_RANDOM));

        flowerType.ifPresent(flower -> {
            ctx.getWorld().setBlockState(blockPos, flower.value().getDefaultState());
        });

        var particlePos = blockPos.toCenterPos();
        ctx.getWorld().spawnParticles(
                ModParticles.PROTECTED_BLOCK, particlePos.x, particlePos.y, particlePos.z,
                1, 0, 0, 0, 0
        );

        return VoidFragment.INSTANCE;
    }
}
