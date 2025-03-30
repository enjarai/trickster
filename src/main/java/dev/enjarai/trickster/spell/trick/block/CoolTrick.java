package dev.enjarai.trickster.spell.trick.block;

import dev.enjarai.trickster.data.DataLoader;
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
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.world.event.GameEvent;

import java.util.List;

public class CoolTrick extends Trick<CoolTrick> {
    public CoolTrick() {
        super(Pattern.of(3, 4, 5, 8, 3, 6, 5, 7, 3), Signature.of(FragmentType.VECTOR, CoolTrick::cool));
    }

    public Fragment cool(SpellContext ctx, VectorFragment pos) throws BlunderException {
        var blockPos = pos.toBlockPos();
        var world = ctx.source().getWorld();
        expectCanBuild(ctx, blockPos);

        var blockState = world.getBlockState(blockPos);

        if (CampfireBlock.isLitCampfire(blockState) || CandleBlock.isLitCandle(blockState) || CandleCakeBlock.isLitCandle(blockState)) {
            ctx.useMana(this, 0.001f);

            world.setBlockState(blockPos, blockState.with(Properties.LIT, false));
            world.emitGameEvent(null, GameEvent.BLOCK_CHANGE, blockPos);
        } else if (!blockState.isAir()) {
            ctx.useMana(this, 80);

            DataLoader.getCoolLoader().convert(blockState.getBlock(), world, blockPos);

            for (Direction direction : Direction.values()) {
                var offsetPos = blockPos.offset(direction);
                if (world.getBlockState(offsetPos).isAir() && Blocks.SNOW.getDefaultState().canPlaceAt(world, offsetPos)) {
                    world.setBlockState(offsetPos, Blocks.SNOW.getDefaultState());
                }
            }
            world.emitGameEvent(null, GameEvent.BLOCK_CHANGE, blockPos);

            var particlePos = blockPos.toCenterPos();
            world.spawnParticles(
                    ParticleTypes.SNOWFLAKE, particlePos.x, particlePos.y, particlePos.z,
                    16, 0.5, 0.5, 0.5, 0
            );
        } else {
            throw new BlockInvalidBlunder(this);
        }

        return pos;
    }
}
