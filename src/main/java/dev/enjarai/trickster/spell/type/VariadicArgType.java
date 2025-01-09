package dev.enjarai.trickster.spell.type;

import java.util.List;

import dev.enjarai.trickster.spell.Fragment;

public interface VariadicArgType<T extends Fragment> extends ArgType<List<T>> {
    VariadicArgType<T> required();

    VariadicArgType<T> unpack();
}
