package dev.enjarai.trickster.datagen.provider;

import com.google.common.collect.Maps;
import dev.enjarai.trickster.data.StateToManaConversionLoader;
import dev.enjarai.trickster.mixin.accessor.TagEntryAccessor;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.Block;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagEntry;
import net.minecraft.registry.tag.TagKey;
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
    private final Map<TagEntry, Builder> builders = Maps.newLinkedHashMap();

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
                                            TagEntryAccessor accessor = (TagEntryAccessor) entry.getKey();
                                            Identifier identifier = accessor.isTag() ? accessor.getId().withPrefixedPath("tags/") : accessor.getId();
                                            Path path = this.pathResolver.resolveJson(identifier);
                                            return DataProvider
                                                    .writeCodecToPath(
                                                            writer,
                                                            wrapperLookup,
                                                            StateToManaConversionLoader.ConversionData.CODEC,
                                                            entry.getValue().build(),
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
        TagEntry tagEntry = TagEntry.create(Registries.BLOCK.getId(block));
        return this.builders.computeIfAbsent(tagEntry, identifier -> Builder.create(tagEntry, block));
    }

    protected Builder getOrCreateConversion(TagKey<Block> tag, Block reference) {
        TagEntry tagEntry = TagEntry.createTag(tag.id());
        return this.builders.computeIfAbsent(tagEntry, identifier -> Builder.create(tagEntry, reference));
    }

    @Override
    public String getName() {
        return "State to Mana Conversion";
    }

    public static class Builder {
        private final TagEntry tag;
        private final Block block;
        private final List<StateToManaConversionLoader.ConversionRule> entries = new ArrayList<>();

        public Builder(TagEntry tag, Block block) {
            this.tag = tag;
            this.block = block;
        }

        public static Builder create(TagEntry tag, Block reference) {
            return new Builder(tag, reference);
        }

        public StateToManaConversionLoader.ConversionData build() {
            return new StateToManaConversionLoader.ConversionData(false, tag, block, List.copyOf(this.entries));
        }

        public Builder add(float mana, Property.Value<?>... properties) {
            this.entries.add(new StateToManaConversionLoader.ConversionRule(List.of(properties), mana));
            return this;
        }
    }
}
