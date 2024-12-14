package dev.enjarai.trickster.datagen.provider;

import com.google.common.collect.Maps;
import dev.enjarai.trickster.data.conversion.BlockConversionLoader;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;

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
                .thenCompose(wrapperLookup -> {
                    return CompletableFuture.allOf(
                            this.builders
                                    .entrySet()
                                    .stream()
                                    .map(
                                            entry -> {
                                                Identifier identifier = entry.getKey();
                                                List<BlockConversionLoader.WeightedValue> values = entry.getValue().build();
                                                Path path = this.pathResolver.resolveJson(identifier);
                                                return DataProvider.writeCodecToPath(writer, wrapperLookup, BlockConversionLoader.Replaceable.CODEC,
                                                        new BlockConversionLoader.Replaceable(false, values), path);
                                            })
                                    .toArray(CompletableFuture[]::new));
                });
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

    //TODO: Change this so you can provide blocks from other mods from their Identifier
    public static class Builder {
        private final List<BlockConversionLoader.WeightedValue> entries = new ArrayList<>();

        public static Builder create() {
            return new Builder();
        }

        public List<BlockConversionLoader.WeightedValue> build() {
            return List.copyOf(this.entries);
        }

        public Builder add(Block block, int weight) {
            this.entries.add(new BlockConversionLoader.WeightedValue(block.getDefaultState(), Optional.empty(), weight));
            return this;
        }

        public Builder add(BlockState state, int weight) {
            this.entries.add(new BlockConversionLoader.WeightedValue(state, Optional.empty(), weight));
            return this;
        }

        public Builder add(Block block, NbtCompound nbt, int weight) {
            this.entries.add(new BlockConversionLoader.WeightedValue(block.getDefaultState(), Optional.ofNullable(nbt), weight));
            return this;
        }

        public Builder add(BlockState state, NbtCompound nbt, int weight) {
            this.entries.add(new BlockConversionLoader.WeightedValue(state, Optional.ofNullable(nbt), weight));
            return this;
        }
    }
}
