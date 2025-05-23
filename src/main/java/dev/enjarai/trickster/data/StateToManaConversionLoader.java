package dev.enjarai.trickster.data;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.enjarai.trickster.Trickster;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.ResourceManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profiler;

import java.util.*;
import java.util.function.Function;

public class StateToManaConversionLoader extends CompleteJsonDataLoader implements IdentifiableResourceReloadListener {
    protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    protected final RegistryWrapper.WrapperLookup registryLookup;
    protected ImmutableMap<Block, List<ConversionRule>> conversions = ImmutableMap.of();

    public StateToManaConversionLoader(RegistryWrapper.WrapperLookup registryLookup) {
        // This uses CODEC in a future version
        // The inline commented code is for easier porting for future versions, as mojang changed this to use codecs
        super(GSON/*Replaceable.CODEC*/, "conversion/state_to_mana");
        this.registryLookup = registryLookup;
    }

    @Override
    public Identifier getFabricId() {
        return Trickster.id("conversion", "state_to_mana");
    }

    @Override
    // The inline commented code is for easier porting for future versions, as mojang changed this to use codecs
    protected void apply(Map<Identifier, List<JsonElement/*Replaceable*/>> prepared, ResourceManager manager, Profiler profiler) {
        RegistryWrapper.Impl<Block> lookup = registryLookup.getWrapperOrThrow(RegistryKeys.BLOCK);
        Map<Block, List<ConversionRule>> map = new HashMap<>();

        prepared.forEach((identifier, jsonElements) -> {
            Block source = lookup.getOrThrow(RegistryKey.of(RegistryKeys.BLOCK, identifier)).value();
            map.compute(source, (block, weightedValues) -> {
                List<ConversionRule> values = Objects.requireNonNullElseGet(weightedValues, ArrayList::new);

                for (JsonElement jsonElement : jsonElements) {
                    Replaceable replaceable = Replaceable.CODEC.apply(source).parse(JsonOps.INSTANCE, jsonElement).getOrThrow();
                    if (replaceable.replace) {
                        values.clear();
                    }

                    values.addAll(replaceable.conversions);
                }

                return values;
            });
        });

        ImmutableMap.Builder<Block, List<ConversionRule>> builder = ImmutableMap.builder();
        map.forEach((block, weightedValues) -> builder.put(block, List.copyOf(weightedValues)));

        conversions = builder.build();
    }

    /**
     * Convert a block state to maybe some mana
     */
    public Optional<Float> convert(BlockState state) {
        List<ConversionRule> rules = conversions.get(state.getBlock());
        if (rules == null) return Optional.empty();

        ruleLoop:
        for (ConversionRule rule : rules) {
            for (Property.Value<?> property : rule.properties()) {
                if (!state.get(property.property()).equals(property.value())) {
                    continue ruleLoop;
                }
            }
            return Optional.of(rule.mana());
        }

        return Optional.empty();
    }

    public record Replaceable(boolean replace, List<ConversionRule> conversions) {
        public static final Function<Block, Codec<Replaceable>> CODEC = Util.memoize(block -> RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("replace", false).forGetter(Replaceable::replace),
            ConversionRule.CODEC.apply(block).listOf().fieldOf("rules").forGetter(Replaceable::conversions)
          ).apply(instance, Replaceable::new)
        ));
    }

    // The properties is an OR list, it is compared in order and the first match for all listed properties is used
    public record ConversionRule(Collection<Property.Value<?>> properties, float mana) {
        /**
         * The codec does not take a list in for properties, it is a map.</br>
         * For example, you will provide:</br>
         *
         * <pre>
         * {@code
         * {
         *   "properties": {
         *     "waterlogged": true
         *   },
         *   "mana": 12
         * }
         * }
         * </pre>
         * <p>
         * Omitting the properties key or providing an empty one will match any block state.</br>
         * The example above matches any state that is waterlogged, independent of other properties.
         */
        public static final Function<Block, Codec<ConversionRule>> CODEC = Util.memoize(block -> {
            Collection<Property<?>> properties = block.getDefaultState().getProperties();
            Map<String, Codec<Property.Value<?>>> propertyCodecs = HashMap.newHashMap(properties.size());

            for (Property<?> property : properties) {
                propertyCodecs.put(property.getName(), (Codec) property.getValueCodec());
            }

            Codec<Map<String, Property.Value<?>>> propertyNamePropertyCodec = Codec.dispatchedMap(Codec.STRING, propertyCodecs::get);
            Codec<Collection<Property.Value<?>>> propertyCodec = propertyNamePropertyCodec.xmap(
              Map::values,
              values -> {
                  HashMap<String, Property.Value<?>> map = HashMap.newHashMap(values.size());
                  for (Property.Value<?> value : values) {
                      map.put(value.property().getName(), value);
                  }

                  return map;
              });

            return RecordCodecBuilder.create(instance -> instance.group(
                propertyCodec.optionalFieldOf("properties", List.of()).forGetter(ConversionRule::properties),
                Codec.FLOAT.fieldOf("mana").forGetter(ConversionRule::mana)
              ).apply(instance, ConversionRule::new)
            );
        });
    }
}
