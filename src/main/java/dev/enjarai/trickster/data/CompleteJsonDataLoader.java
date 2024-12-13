package dev.enjarai.trickster.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.Map.Entry;

import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;

/**
 * An abstract implementation of resource reloader that reads JSON files into Gson representations in the prepare stage.
 */
public abstract class CompleteJsonDataLoader extends SinglePreparationResourceReloader<Map<Identifier, List<JsonElement>>> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Gson gson;
    private final String dataType;

    // Reminder: This will need to be changed/replaced on a newer version as Mojang changed it
    // I think it was 1.20.3, idk, never bothered checking
    public CompleteJsonDataLoader(Gson gson, String dataType) {
        this.gson = gson;
        this.dataType = dataType;
    }

    protected Map<Identifier, List<JsonElement>> prepare(ResourceManager resourceManager, Profiler profiler) {
        Map<Identifier, List<JsonElement>> map = new HashMap<>();
        load(resourceManager, this.dataType, this.gson, map);
        return map;
    }

    public static void load(ResourceManager manager, String dataType, Gson gson, Map<Identifier, List<JsonElement>> results) {
        ResourceFinder resourceFinder = ResourceFinder.json(dataType);

        for (Entry<Identifier, List<Resource>> entry : resourceFinder.findAllResources(manager).entrySet()) {
            Identifier key = entry.getKey();
            Identifier resourceId = resourceFinder.toResourceId(key);

            try {
                for (Resource resource : entry.getValue()) {
                    Reader reader = resource.getReader();

                    try {
                        JsonElement jsonElement = JsonHelper.deserialize(gson, reader, JsonElement.class);
                        results.compute(resourceId, (identifier, jsonElements) -> {
                            List<JsonElement> list;
                            list = Objects.requireNonNullElseGet(jsonElements, ArrayList::new);
                            list.add(jsonElement);
                            return list;
                        });
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
