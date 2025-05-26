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

    public static <T1 extends Fragment, T2 extends Fragment, T3 extends Fragment, T4 extends Fragment> boolean fuzzyEquals(io.vavr.collection.HashMap<T1, T2> a, io.vavr.collection.HashMap<T3, T4> b) {
        return a.equals(b); //TODO: implement
    }

    public static <T extends Fragment> int fuzzyHash(java.util.List<T> items) {
        int result = 1;

        for (var item : items) {
            result = 31 * result + item.fuzzyHash();
        }

        return result;
    }

    public static <T1 extends Fragment, T2 extends Fragment> int fuzzyHash(io.vavr.collection.HashMap<T1, T2> map) {
        return map.hashCode(); //TODO: implement
    }
}
