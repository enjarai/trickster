package dev.enjarai.trickster.spell.trick.block;

import dev.enjarai.trickster.data.DataLoader;
import dev.enjarai.trickster.pond.FuelableFurnaceDuck;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlockInvalidBlunder;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.block.*;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.world.event.GameEvent;

import java.util.List;

public class HeatTrick extends Trick<HeatTrick> {
    public HeatTrick() {
        super(Pattern.of(3, 4, 5, 2, 3, 0, 5, 1, 3),
                Signature.of(FragmentType.VECTOR, HeatTrick::heat));
    }





    public Fragment heat(SpellContext ctx,              VectorFragment pos)

            throws BlunderException {
        var blockPos = pos.toBlockPos();
        var world =       ctx.source()      .getWorld();
        expectCanBuild(ctx,



                blockPos);

        var blockState = world.   getBlockState  (blockPos


        );

        if (CampfireBlock.canBeLit(blockState) || CandleBlock.canBeLit(blockState) || CandleCakeBlock.canBeLit(blockState)) {
            ctx.useMana(this, 0.001f);

            world.setBlockState(blockPos, blockState.with(Properties.LIT, true));
            world.emitGameEvent(null, GameEvent.BLOCK_CHANGE, blockPos);
        } else if (!blockState.isAir()) {
            ctx.useMana(this, 80);

            if (blockState.getBlock() instanceof TntBlock) {
                ctx.useMana(this, 80);

                TntBlock.primeTnt(world, blockPos);
                world.removeBlock(blockPos, false);
            } else if (blockState.getBlock() instanceof AbstractFurnaceBlock && world.getBlockEntity(blockPos) instanceof AbstractFurnaceBlockEntity furnace) {
                ((FuelableFurnaceDuck) furnace).trickster$setFuelLevelAtLeast(1601);
            } else {
                DataLoader.getHeatLoader().convert(blockState.getBlock(), world, blockPos);
            }

            for (Direction direction : Direction.values()) {
                var offsetPos = blockPos.offset(direction);
                if (AbstractFireBlock.canPlaceAt(world, offsetPos, direction)) {
                    world.setBlockState(offsetPos, AbstractFireBlock.getState(world, offsetPos));
                }
            }
            world.emitGameEvent(null, GameEvent.BLOCK_CHANGE, blockPos);

            var particlePos = blockPos.toCenterPos();
            world.spawnParticles(
                    ParticleTypes.FLAME, particlePos.x, particlePos.y, particlePos.z,
                    16, 0.5, 0.5, 0.5, 0
            );
        } else {
            throw new BlockInvalidBlunder(this);
        }

        return pos;
    }
}
