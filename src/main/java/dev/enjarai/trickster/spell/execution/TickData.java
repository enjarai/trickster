package dev.enjarai.trickster.spell.execution;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import dev.enjarai.trickster.Trickster;
import net.minecraft.util.Identifier;

public class TickData {
    private Map<Key<?>, Object> map = new HashMap<>();
    private int executions = 0;
    private int slot = -1;
    
    public void incrementExecutions() {
        executions++;
    }

    public int getExecutions() {
        return executions;
    }

    public int getSlot() {
        return slot;
    }

    public boolean isExecutionLimitReached() {
        return executions >= Trickster.CONFIG.maxExecutionsPerSpellPerTick();
    }

    public TickData withSlot(int slot) {
        this.slot = slot;
        return this;
    }

    public TickData withBonusExecutions(int executions) {
        this.executions = -executions;
        return this;
    }

    public record Key<T>(Identifier id, @Nullable Class<T> clazz) {
        @SuppressWarnings("unchecked")
        public Optional<T> get(TickData data) {
            return Optional.ofNullable(data.map.get(this)).map(n -> (T) n);
        }

        public T set(TickData data, T value) {
            data.map.put(this, value);
            return value;
        }
    }
}
