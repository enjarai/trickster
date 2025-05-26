package dev.enjarai.trickster.util;

import dev.enjarai.trickster.spell.Fragment;

public class FuzzyUtils {
    public static <T1 extends Fragment, T2 extends Fragment> boolean fuzzyEquals(java.util.List<T1> a, java.util.List<T2> b) {
        if (a.size() != b.size()) return false;

        for (int i = 0; i < a.size(); i++) {
            if (!a.get(i).fuzzyEquals(b.get(i))) return false;
        }

        return true;
    }

    public static <K extends Fragment, V extends Fragment> boolean fuzzyEquals(io.vavr.collection.HashMap<K, V> a, io.vavr.collection.HashMap<K, V> b) {
        if (a.size() != b.size()) return false;

        for (var kv : a) {
            if (!b.get(kv._1).map(v -> kv._2.fuzzyEquals(v)).getOrElse(() -> false)) return false;
        }

        return true;
    }

    public static <T extends Fragment> int fuzzyHash(java.util.List<T> items) {
        int result = 1;

        for (var item : items) {
            result = 31 * result + item.fuzzyHash();
        }

        return result;
    }

    public static <T1 extends Fragment, T2 extends Fragment> int fuzzyHash(io.vavr.collection.HashMap<T1, T2> map) {
        int result = 0;

        for (var kv : map) {
            result += kv._1.fuzzyHash() + kv._2.fuzzyHash();
        }

        return result;
    }
}
