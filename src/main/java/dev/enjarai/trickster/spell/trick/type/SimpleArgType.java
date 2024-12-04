package dev.enjarai.trickster.spell.trick.type;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import dev.enjarai.trickster.spell.Fragment;

public class SimpleArgType<T extends Fragment> implements ArgType<T> {
    private final Class<T> type;

    public SimpleArgType(Class<T> type) {
        this.type = type;
    }
    @Override
    public int argc(List<Fragment> fragments) {
        return 1;
    }

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public T compose(List<Fragment> fragments) {
        var result = fragments.get(0);

        if (type.isInstance(result)) {
            return (T) result;
        }

        return null;
    }
}
