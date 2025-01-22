package dev.enjarai.trickster.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.Map.Entry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;

/**
 * An abstract implementation of resource reloader that reads JSON files
 * into Gson representations in the prepare stage.
 */
public abstract class CompleteJsonDataLoader<T> extends SinglePreparationResourceReloader<Map<Identifier, List<T>>> {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final DynamicOps<JsonElement> ops;
	private final Codec<T> codec;
	private final ResourceFinder finder;

	protected CompleteJsonDataLoader(RegistryWrapper.WrapperLookup registries, Codec<T> codec, RegistryKey<? extends Registry<T>> registryRef) {
		this(registries.getOps(JsonOps.INSTANCE), codec, ResourceFinder.json(registryRef));
	}

	protected CompleteJsonDataLoader(Codec<T> codec, ResourceFinder finder) {
		this(JsonOps.INSTANCE, codec, finder);
	}

	public CompleteJsonDataLoader(DynamicOps<JsonElement> ops, Codec<T> codec, ResourceFinder finder) {
		this.ops = ops;
		this.codec = codec;
		this.finder = finder;
	}

	protected Map<Identifier, List<T>> prepare(ResourceManager resourceManager, Profiler profiler) {
		Map<Identifier, List<T>> map = new HashMap<>();
		load(resourceManager, this.finder, this.ops, this.codec, map);
		return map;
	}

	public static <T> void load(ResourceManager manager, ResourceFinder finder, DynamicOps<JsonElement> ops, Codec<T> codec, Map<Identifier, List<T>> results) {
		for (Entry<Identifier, List<Resource>> entry : finder.findAllResources(manager).entrySet()) {
			Identifier key = entry.getKey();
			Identifier resourceId = finder.toResourceId(key);

			try {
				for (Resource resource : entry.getValue()) {
					Reader reader = resource.getReader();

					try {
						codec.parse(ops, JsonParser.parseReader(reader)).ifSuccess((value) -> {
							results.compute(resourceId, (identifier, elements) -> {
								List<T> list;
								list = Objects.requireNonNullElseGet(elements, ArrayList::new);
								list.add(value);
								return list;
							});
						}).ifError((error) -> LOGGER.error("Couldn't parse data file '{}' from '{}': {}", key, resourceId, error));
					} catch (Throwable exception) {
						if (reader != null) {
							try {
								reader.close();
							} catch (Throwable e) {
								exception.addSuppressed(e);
							}
						}

						throw exception;
					}

					reader.close();
				}

			} catch (IllegalArgumentException | IOException | JsonParseException e) {
				LOGGER.error("Couldn't parse data file {} from {}", resourceId, key, e);
			}

		}

		var copy = Map.copyOf(results);
		results.clear();
		copy.forEach((identifier, jsonElements) -> {
			results.put(identifier, List.copyOf(jsonElements));
		});
	}
}
