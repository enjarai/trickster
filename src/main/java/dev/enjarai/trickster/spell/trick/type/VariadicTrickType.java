package dev.enjarai.trickster.spell.trick.type;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import dev.enjarai.trickster.spell.Fragment;

public class VariadicTrickType<T extends Fragment> implements ArgType<List<T>> {
    private final Class<T>[] types;
    
    @SuppressWarnings("unchecked")
    public VariadicTrickType(Class<T>... types) {
        this.types = types;
    }

    @Override
    public int argc(List<Fragment> fragments) {
        return fragments.size();
    }

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public List<T> compose(List<Fragment> fragments) {
        var result = new ArrayList<T>();
        int offset = 0;

        for (var fragment : fragments) {
            if (types[offset % types.length].isInstance(fragment)) {
                result.add((T) fragment);
            } else {
                return null;
            }

            offset++;
        }

        return result;
    }
}
