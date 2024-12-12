package dev.enjarai.trickster.spell.fragment;

import com.mojang.datafixers.optics.profunctors.FunctorProfunctor;
import org.jetbrains.annotations.Contract;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;

//i tried recursive generics, and it was a pain :C
public class TypeTrie {
    /*
    TrieNode<Object> root = new TrieNode<>();

    public TypeTrie append(FragmentType[] path, Object method) {
        var node = root;

        for (FragmentType step : path) {
            node.children.putIfAbsent(step, new TrieNode<>());
            node = node.children.get(step);
        }

        node.data = Optional.of(method);

        return this;
    }

    public Object longestCommonPrefix(FragmentType[] signature) {
        var candidate = root;
        var node = root;

        for (FragmentType step : signature) {
            node.c
        }

        return null;
    }
}

class TrieNode<Data> {
    Map<FragmentType, TrieNode> children;
    Optional<Data> data;

    TrieNode() {
        data = Optional.empty();
        children = Map.of();
    }

    TrieNode(Data data) {
        this.data = Optional.of(data);
        children = Map.of();

    }
     */
}
