package dev.enjarai.trickster.data.conversion;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.data.CompleteJsonDataLoader;
import dev.enjarai.trickster.mixin.accessor.StateAccessor;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.ResourceManager;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;
import nl.enjarai.cicada.api.util.random.RandomUtil;
import nl.enjarai.cicada.api.util.random.Weighted;

import java.util.*;
import java.util.function.Function;

public abstract class BlockConversionLoader extends CompleteJsonDataLoader implements IdentifiableResourceReloadListener {
    protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    protected final RegistryWrapper.WrapperLookup registryLookup;
    private final String type;
    protected ImmutableMap<Block, List<WeightedValue>> conversions = ImmutableMap.of();

    public BlockConversionLoader(String dataType, RegistryWrapper.WrapperLookup registryLookup) {
        // This uses CODEC in a future version
        // The inline commented code is for easier porting for future versions, as mojang changed this to use codecs
        super(GSON/*Replaceable.CODEC*/, "conversion/" + dataType);
        this.registryLookup = registryLookup;
        this.type = dataType;
    }

    @Override
    public Identifier getFabricId() {
        return Trickster.id("conversion", type);
    }

    @Override
    // The inline commented code is for easier porting for future versions, as mojang changed this to use codecs
    protected void apply(Map<Identifier, List<JsonElement/*Replaceable*/>> prepared, ResourceManager manager, Profiler profiler) {
        RegistryWrapper.Impl<Block> lookup = registryLookup.getWrapperOrThrow(RegistryKeys.BLOCK);
        Map<Block, List<WeightedValue>> map = new HashMap<>();

        prepared.forEach((identifier, jsonElements) -> {
            Block source = lookup.getOrThrow(RegistryKey.of(RegistryKeys.BLOCK, identifier)).value();
            map.compute(source, (block, weightedValues) -> {
                List<WeightedValue> values = Objects.requireNonNullElseGet(weightedValues, ArrayList::new);

                for (JsonElement jsonElement : jsonElements) {
                    Replaceable replaceable = Replaceable.CODEC.parse(JsonOps.INSTANCE, jsonElement).getOrThrow();
                    if (replaceable.replace) {
                        values.clear();
                    }

                    values.addAll(replaceable.conversions);
                }

                return values;
            });
        });

        ImmutableMap.Builder<Block, List<WeightedValue>> builder = ImmutableMap.builder();
        map.forEach((block, weightedValues) -> {
            builder.put(block, List.copyOf(weightedValues));
        });

        conversions = builder.build();
    }

    public boolean convert(Block block, World world, BlockPos pos) {
        List<WeightedValue> values = conversions.get(block);
        if (values == null) return false;

        Optional<WeightedValue> perhapsWeightedValue = RandomUtil.chooseWeighted(values);
        if (perhapsWeightedValue.isEmpty()) return false;

        WeightedValue weightedValue = perhapsWeightedValue.get();

        BlockEntity blockEntity;
        BlockState blockState = Block.postProcessState(weightedValue.state(), world, pos);

        if (blockState.isAir()) {
            world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, pos, Block.getRawIdFromState(blockState));
        }

        if (weightedValue.keepProperties.isPresent()) {
            Either<List<String>, Boolean> either = weightedValue.keepProperties.get();
            Function<String, Boolean> checker;
            if (either.right().isPresent()) {
                checker = (name) -> either.right().get();
            } else {
                checker = (name) -> either.left().get().contains(name);
            }

            BlockState oldState = world.getBlockState(pos);
            for (Property<?> property : blockState.getEntries().keySet()) {
                if (checker.apply(property.getName()) && oldState.contains(property)) {
                    blockState = propertyFromOldBlock(blockState, oldState, property);
                }
            }
        }

        if (blockState.getFluidState().isOf(Fluids.WATER) && world.getDimension().ultrawarm()) {
            if (blockState.isOf(Blocks.WATER)) {
                blockState = Blocks.AIR.getDefaultState();
            } else {
                Optional<Boolean> perhapsWaterlogged = blockState.getOrEmpty(Properties.WATERLOGGED);
                if (perhapsWaterlogged.isPresent() && perhapsWaterlogged.get()) {
                    blockState = blockState.with(Properties.WATERLOGGED, false);
                }
            }
        }

        if (!world.setBlockState(pos, blockState)) {
            return false;
        }

        world.emitGameEvent(null, GameEvent.BLOCK_CHANGE, pos);

        if (weightedValue.nbt().isPresent() && (blockEntity = world.getBlockEntity(pos)) != null) {
            blockEntity.read(weightedValue.nbt().get(), world.getRegistryManager());
        }
        return true;
    }

    private <T extends Comparable<T>> BlockState propertyFromOldBlock(BlockState newState, BlockState oldState, Property<T> property) {
        return newState.with(property, oldState.get(property));
    }

    public record Replaceable(boolean replace, List<WeightedValue> conversions) {
        public static final Codec<Replaceable> CODEC = RecordCodecBuilder.create(instance ->
          instance.group(
            Codec.BOOL.optionalFieldOf("replace", false).forGetter(Replaceable::replace),
            WeightedValue.CODEC.listOf().fieldOf("conversions").forGetter(Replaceable::conversions)
          ).apply(instance, Replaceable::new)
        );
    }

    public record WeightedValue(BlockState state, Optional<Either<List<String>, Boolean>> keepProperties, Optional<NbtCompound> nbt, int weight) implements Weighted {
        public static final MapCodec<BlockState> BLOCK_STATE_CODEC = Registries.BLOCK.getCodec().dispatchMap("id", state -> ((StateAccessor) state).getOwner(), owner -> {
            BlockState state = owner.getDefaultState();
            if (state.getEntries().isEmpty()) {
                return MapCodec.unit(state);
            }
            return ((StateAccessor) state).<BlockState>getCodec().codec().optionalFieldOf("properties").xmap(optional -> optional.orElse(state), Optional::of);
        }).stable();

        public static final Codec<WeightedValue> CODEC = RecordCodecBuilder.create(instance ->
          instance.group(
            RecordCodecBuilder.of(WeightedValue::state, BLOCK_STATE_CODEC),
            Codec.either(Codec.STRING.listOf(), Codec.BOOL).optionalFieldOf("keepProperties").forGetter(WeightedValue::keepProperties),
            NbtCompound.CODEC.optionalFieldOf("nbt").forGetter(WeightedValue::nbt),
            Codec.INT.fieldOf("weight").forGetter(WeightedValue::weight)
          ).apply(instance, WeightedValue::new)
        );

        @Override
        public double getWeight() {
            return weight;
        }
    }
}
