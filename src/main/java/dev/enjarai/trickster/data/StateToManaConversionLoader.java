package dev.enjarai.trickster.data;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.mixin.accessor.DataPackContentsAccessor;
import dev.enjarai.trickster.mixin.accessor.TagEntryAccessor;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagEntry;
import net.minecraft.registry.tag.TagManagerLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.DataPackContents;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class StateToManaConversionLoader extends CompleteJsonDataLoader implements IdentifiableResourceReloadListener {
    public static final Logger LOGGER = LoggerFactory.getLogger(StateToManaConversionLoader.class);
    protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static DataPackContents dataPackContents;
    protected final RegistryWrapper.WrapperLookup registryLookup;
    protected ImmutableMap<Block, List<ConversionRule>> conversions = ImmutableMap.of();

    public StateToManaConversionLoader(RegistryWrapper.WrapperLookup registryLookup) {
        super(GSON, "conversion/state_to_mana");
        this.registryLookup = registryLookup;
    }

    @Override
    public Identifier getFabricId() {
        return Trickster.id("conversion", "state_to_mana");
    }

    @Override
    protected void apply(Map<Identifier, List<JsonElement>> prepared, ResourceManager manager, Profiler profiler) {
        Map<Block, List<ConversionRule>> map = new HashMap<>();

        var perhapsTagMap = ((DataPackContentsAccessor) dataPackContents)
                .getRegistryTagManager().getRegistryTags().stream()
                .filter(registryTags -> registryTags.key().equals(RegistryKeys.BLOCK))
                .map(TagManagerLoader.RegistryTags::tags).findFirst();

        if (perhapsTagMap.isEmpty()) {
            throw new IllegalStateException("Failed to get tag data, aborting data loading");
        }

        //noinspection unchecked,rawtypes
        Map<Identifier, Collection<RegistryEntry<Block>>> tagMap = (Map) perhapsTagMap.get();

        prepared.forEach((identifier, jsonElements) -> {
            for (JsonElement jsonElement : jsonElements) {
                // Add identifier to the json so the codec can see it
                jsonElement.getAsJsonObject().addProperty("identifier", identifier.toString());

                ConversionData conversionData = ConversionData.CODEC.parse(JsonOps.INSTANCE, jsonElement).getOrThrow();

                TagEntryAccessor accessor = (TagEntryAccessor) conversionData.target;
                if (accessor.isTag()) {
                    Optional<Collection<RegistryEntry<Block>>> optional = Optional.ofNullable(tagMap.get(accessor.getId()));

                    if (optional.isPresent()) {
                        for (RegistryEntry<Block> entry : optional.get()) {
                            if (conversionData.replace) {
                                map.put(entry.value(), conversionData.rules);
                            } else {
                                map.merge(entry.value(), conversionData.rules, (rules1, rules2) -> {
                                    List<ConversionRule> concatenatedList = new ArrayList<>();
                                    concatenatedList.addAll(rules1);
                                    concatenatedList.addAll(rules2);
                                    return concatenatedList;
                                });
                            }
                        }
                    } else {
                        LOGGER.warn("Failed to find tag {}, skipping", accessor.getId());
                    }
                } else {
                    Block block = Registries.BLOCK.get(accessor.getId());

                    if (conversionData.replace) {
                        map.put(block, conversionData.rules);
                    } else {
                        map.merge(block, conversionData.rules, (rules1, rules2) -> {
                            List<ConversionRule> concatenatedList = new ArrayList<>();
                            concatenatedList.addAll(rules1);
                            concatenatedList.addAll(rules2);
                            return concatenatedList;
                        });
                    }
                }
            }
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

    public record ConversionData(boolean replace, TagEntry target, Block reference, List<ConversionRule> rules) {
        public static final MapDecoder<ConversionData> CODEC_DECODER = new MapDecoder.Implementation<>() {

            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return Stream.of(
                        ops.createString("target"),
                        ops.createString("block"),
                        ops.createString("rules")
                );
            }

            @Override
            public <T> DataResult<ConversionData> decode(DynamicOps<T> ops, MapLike<T> input) {
                T replaceOp = input.get("replace");
                boolean replace = false;
                if (replaceOp != null) {
                    replace = Codec.BOOL.parse(ops, input.get("replace")).getOrThrow();
                }

                Identifier identifier = Identifier.CODEC.parse(ops, input.get("identifier")).getOrThrow();

                TagEntry target;
                Identifier block = identifier;
                T targetOp = input.get("target");
                if (targetOp != null) {
                    target = TagEntry.CODEC.parse(ops, targetOp).getOrThrow();
                    if (((TagEntryAccessor) target).isTag()) {
                        block = Identifier.CODEC.parse(ops, input.get("block")).getOrThrow();
                    }
                } else {
                    target = TagEntry.create(identifier);
                }

                Block reference = Registries.BLOCK.get(block);

                List<ConversionRule> rules = ConversionRule.CODEC.apply(reference).listOf().parse(ops, input.get("rules")).getOrThrow();

                return DataResult.success(new ConversionData(replace, target, reference, rules));
            }
        };
        public static final MapEncoder<ConversionData> CODEC_ENCODER = new MapEncoder.Implementation<>() {

            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return Stream.of(
                        ops.createString("target"),
                        ops.createString("block"),
                        ops.createString("rules")
                );
            }

            @Override
            public <T> RecordBuilder<T> encode(ConversionData input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                Identifier block = Registries.BLOCK.getId(input.reference);
                prefix.add("replace", Codec.BOOL.encodeStart(ops, input.replace));
                prefix.add("target", TagEntry.CODEC.encodeStart(ops, input.target));
                if (((TagEntryAccessor) input.target).isTag()) {
                    prefix.add("block", Identifier.CODEC.encodeStart(ops, block));
                }
                prefix.add("rules", ConversionRule.CODEC.apply(input.reference).listOf().encodeStart(ops, input.rules));

                return prefix;
            }
        };
        public static final Codec<ConversionData> CODEC = Codec.of(CODEC_ENCODER, CODEC_DECODER).codec();
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
