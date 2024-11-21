package dev.enjarai.trickster.spell.trick.block;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlockInvalidBlunder;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.block.*;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;

import java.util.List;
import java.util.Optional;

public class ErosionTrick extends Trick {
    public ErosionTrick() {
        super(Pattern.of(0, 4, 6, 7, 8, 4, 2));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var weatheringPosFragment = expectInput(fragments, FragmentType.VECTOR, 0);
        var weatheringPos = weatheringPosFragment.toBlockPos();
        var waterPos = expectInput(fragments, FragmentType.VECTOR, 1).toBlockPos();

        expectCanBuild(ctx, weatheringPos);
        expectCanBuild(ctx, waterPos);

        ServerWorld world = ctx.source().getWorld();

        BlockState blockState = world.getBlockState(weatheringPos);

        BlockState state = world.getBlockState(waterPos);

        if ((!state.isOf(Blocks.WATER_CAULDRON) || state.get(LeveledCauldronBlock.LEVEL) != 3) && !state.getFluidState().isOf(Fluids.WATER)) {
            throw new BlockInvalidBlunder(this, state.getBlock());
        }

        if (!blockState.isAir()) {
            Random random = ctx.source().getWorld().getRandom();

            var tag = TagKey.of(RegistryKeys.BLOCK, Registries.BLOCK.getId(blockState.getBlock()).withPrefixedPath("trickster/conversion/erosion/"));
            if (Registries.BLOCK.getEntryList(tag).isEmpty()) {
                throw new BlockInvalidBlunder(this, blockState.getBlock());
            }

            Optional<RegistryEntry<Block>> conversion;
            conversion = Registries.BLOCK.getEntryList(tag).flatMap(e -> e.getRandom(random));

            if (conversion.isEmpty()) {
                throw new BlockInvalidBlunder(this, blockState.getBlock());
            }

            ctx.useMana(this, 80);

            if (state.isOf(Blocks.WATER_CAULDRON)) {
                world.setBlockState(waterPos, Blocks.CAULDRON.getDefaultState());
            } else if (state.getFluidState().isOf(Fluids.WATER)) {
                if (state.getProperties().contains(Properties.WATERLOGGED)) {
                    world.setBlockState(waterPos, state.with(Properties.WATERLOGGED, false));
                } else if (!state.isOf(Blocks.WATER)) {
                    world.breakBlock(waterPos, true, ctx.source().getCaster().orElse(null));
                    world.setBlockState(waterPos, Blocks.AIR.getDefaultState());
                } else {
                    world.setBlockState(waterPos, Blocks.AIR.getDefaultState());
                }
            }

            RegistryEntry<Block> blockRegistryEntry = conversion.get();

            BlockState defaultState = blockRegistryEntry.value().getDefaultState();
            if (defaultState.isAir()) {
                world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, weatheringPos, Block.getRawIdFromState(blockState));
            }
            world.setBlockState(weatheringPos, defaultState);

            world.emitGameEvent(null, GameEvent.BLOCK_CHANGE, weatheringPos);
        } else {
            throw new BlockInvalidBlunder(this, blockState.getBlock());
        }

        return weatheringPosFragment;
    }
}
