package dev.enjarai.trickster.datagen.provider;

import com.google.common.collect.Maps;
import dev.enjarai.trickster.data.StateToManaConversionLoader;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.Block;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class StateToManaConversionProvider implements DataProvider {
    protected final DataOutput.PathResolver pathResolver;
    private final CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture;
    private final Map<Block, Builder> builders = Maps.newLinkedHashMap();

    public StateToManaConversionProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
        this.pathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, "conversion/state_to_mana");
        this.registryLookupFuture = registryLookupFuture;
    }

    protected abstract void configure(RegistryWrapper.WrapperLookup wrapperLookup);

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        return this.getRegistryLookupFuture()
                .thenCompose(wrapperLookup -> CompletableFuture.allOf(
                        this.builders
                                .entrySet()
                                .stream()
                                .map(
                                        entry -> {
                                            Identifier identifier = Registries.BLOCK.getId(entry.getKey());
                                            List<StateToManaConversionLoader.ConversionRule> values = entry.getValue().build();
                                            Path path = this.pathResolver.resolveJson(identifier);
                                            return DataProvider
                                                    .writeCodecToPath(
                                                            writer,
                                                            wrapperLookup,
                                                            StateToManaConversionLoader.Replaceable.CODEC.apply(entry.getKey()),
                                                            new StateToManaConversionLoader.Replaceable(false, values),
                                                            path
                                                    );
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

    protected Builder getOrCreateConversion(Block block) {
        return this.builders.computeIfAbsent(block, identifier -> Builder.create());
    }

    @Override
    public String getName() {
        return "State to Mana Conversion";
    }

    public static class Builder {
        private final List<StateToManaConversionLoader.ConversionRule> entries = new ArrayList<>();

        public static Builder create() {
            return new Builder();
        }

        public List<StateToManaConversionLoader.ConversionRule> build() {
            return List.copyOf(this.entries);
        }

        public Builder add(float mana, Property.Value<?>... properties) {
            this.entries.add(new StateToManaConversionLoader.ConversionRule(List.of(properties), mana));
            return this;
        }
    }
}
