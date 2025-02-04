package dev.enjarai.trickster.datagen.provider;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.google.common.collect.Maps;

import com.mojang.datafixers.util.Either;
import dev.enjarai.trickster.data.conversion.BlockConversionLoader;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

public abstract class BlockConversionProvider implements DataProvider {
    protected final DataOutput.PathResolver pathResolver;
    private final String type;
    private final CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture;
    private final Map<Identifier, Builder> builders = Maps.newLinkedHashMap();

    public BlockConversionProvider(FabricDataOutput output, String type, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
        this.pathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, "conversion/" + type);
        this.registryLookupFuture = registryLookupFuture;
        this.type = type;
    }

    protected abstract void configure(RegistryWrapper.WrapperLookup wrapperLookup);

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        return this.getRegistryLookupFuture()
          .thenCompose(wrapperLookup ->
            CompletableFuture.allOf(
              this.builders
                .entrySet()
                .stream()
                .map(
                  entry -> {
                      Identifier identifier = entry.getKey();
                      List<BlockConversionLoader.WeightedValue> values = entry.getValue().build();
                      Path path = this.pathResolver.resolveJson(identifier);
                      return DataProvider
                        .writeCodecToPath(writer, wrapperLookup, BlockConversionLoader.Replaceable.CODEC, new BlockConversionLoader.Replaceable(false, values), path);
                  }
                )
                .toArray(CompletableFuture[]::new)
            ));
    }

    protected CompletableFuture<RegistryWrapper.WrapperLookup> getRegistryLookupFuture() {
        return this.registryLookupFuture.thenApply(lookup -> {
            this.builders.clear();
            this.configure(lookup);
            return lookup;
        });
    }

    protected Builder getOrCreateConversion(Block source) {
        Identifier id = Registries.BLOCK.getId(source);
        return this.builders.computeIfAbsent(id, identifier -> Builder.create());
    }

    @Override
    public String getName() {
        return "Block Conversion (" + type + ")";
    }

    // Maybe make this generated?
    public static class Builder {
        private final List<BlockConversionLoader.WeightedValue> entries = new ArrayList<>();

        public static Builder create() {
            return new Builder();
        }

        public List<BlockConversionLoader.WeightedValue> build() {
            return List.copyOf(this.entries);
        }

        public Builder add(Block block, int weight) {
            add(block.getDefaultState(), weight);
            return this;
        }

        public Builder add(BlockState state, int weight) {
            this.entries.add(new BlockConversionLoader.WeightedValue(state, Optional.empty(), Optional.empty(), weight));
            return this;
        }

        public Builder add(Block block, List<String> keepProperties, int weight) {
            add(block.getDefaultState(), keepProperties, weight);
            return this;
        }

        public Builder add(BlockState state, List<String> keepProperties, int weight) {
            this.entries.add(new BlockConversionLoader.WeightedValue(state, Optional.of(Either.left(keepProperties)), Optional.empty(), weight));
            return this;
        }

        public Builder add(Block block, boolean keepProperties, int weight) {
            add(block.getDefaultState(), keepProperties, weight);
            return this;
        }

        public Builder add(BlockState state, boolean keepProperties, int weight) {
            this.entries.add(new BlockConversionLoader.WeightedValue(state, Optional.of(Either.right(keepProperties)), Optional.empty(), weight));
            return this;
        }

        public Builder add(Block block, NbtCompound nbt, int weight) {
            return add(block.getDefaultState(), nbt, weight);
        }

        public Builder add(BlockState state, NbtCompound nbt, int weight) {
            this.entries.add(new BlockConversionLoader.WeightedValue(state, Optional.empty(), Optional.ofNullable(nbt), weight));
            return this;
        }

        public Builder add(Block block, List<String> keepProperties, NbtCompound nbt, int weight) {
            return add(block.getDefaultState(), keepProperties, nbt, weight);
        }

        public Builder add(BlockState state, List<String> keepProperties, NbtCompound nbt, int weight) {
            this.entries.add(new BlockConversionLoader.WeightedValue(state, Optional.of(Either.left(keepProperties)), Optional.ofNullable(nbt), weight));
            return this;
        }

        public Builder add(Block block, boolean keepProperties, NbtCompound nbt, int weight) {
            return add(block.getDefaultState(), keepProperties, nbt, weight);
        }

        public Builder add(BlockState state, boolean keepProperties, NbtCompound nbt, int weight) {
            this.entries.add(new BlockConversionLoader.WeightedValue(state, Optional.of(Either.right(keepProperties)), Optional.ofNullable(nbt), weight));
            return this;
        }
    }
}
