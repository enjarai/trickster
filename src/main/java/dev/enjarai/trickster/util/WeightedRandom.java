package dev.enjarai.trickster.util;

import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class WeightedRandom<T> {
    private final NavigableMap<Double, T> map = new TreeMap<>();
    private final Map<T, Double> weights = new HashMap<>();
    private final Random random;
    private double total = 0;
    private boolean lock;

    public WeightedRandom() {
        this(new Random());
    }

    public WeightedRandom(Random random) {
        this.random = random;
    }

    /**
     * Get the percentages of all the results.
     *
     * @return A map of all the results and their percentages.
     */
    public Map<T, Double> percentages() {
        return weights.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), entry.getValue() * 100 / total))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Get the percentage of a result.
     *
     * @param type The result to get the percentage of.
     *
     * @return The percentage of the result.
     */
    public double percentage(T type) {
        return getWeight(type) * 100 / total;
    }

    /**
     * Get the weight of a result.
     *
     * @param type The result to get the weight of.
     *
     * @return The weight of the result.
     */
    public double getWeight(T type) {
        return weights.getOrDefault(type, 0d);
    }

    public double getTotal() {
        return total;
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Adds a result to the map.
     * <br/>
     * This will not add a result if it already exists, instead of adding multiple of the same result add it with a higher weight.
     *
     * @param weight The weight of the result.
     * @param result The result to add.
     *
     * @return The WeightedRandom instance.
     */
    public synchronized WeightedRandom<T> add(double weight, T result) {
        if (this.lock) {
            throw new IllegalStateException("Can not add value to locked weighted random!");
        }

        if (weights.containsKey(result)) return this;
        if (weight <= 0) return this;
        weights.put(result, weight);
        total += weight;
        map.put(total, result);
        return this;
    }

    /**
     * Updates the weight of a result.
     * <br/>
     * WARNING: This will REBUILD the entire map, so it is not recommended to use this method often.
     *
     * @param weight The weight of the result.
     * @param result The result to add.
     *
     * @return The WeightedRandom instance.
     */
    @ApiStatus.Experimental
    public synchronized WeightedRandom<T> update(double weight, T result) {
        if (this.lock) {
            throw new IllegalStateException("Can not update a value on a locked weighted random!");
        }

        //Nuke the map
        map.clear();
        weights.put(result, weight);
        //Rebuild the map
        for (Map.Entry<T, Double> entry : weights.entrySet()) {
            add(entry.getValue(), entry.getKey());
        }

        return this;
    }

    /**
     * Get a random result from the map.
     *
     * @return A random result.
     */
    public T next() {
        if (map.isEmpty()) return null;
        double value = random.nextDouble() * total;
        return map.higherEntry(value).getValue();
    }

    /**
     * Reset the WeightedRandom instance.
     * This will clear the maps, clear the total weight and clear the random seed.
     */
    public void clear() {
        if (this.lock) {
            throw new IllegalStateException("Can not clear a locked weighted random!");
        }
        map.clear();
        weights.clear();
        random.setSeed(random.nextLong());
        total = 0;
    }

    public void forEach(BiConsumer<T, Double> action) {
        weights.forEach(action);
    }

    public void lock() {
        this.lock = true;
    }
}
