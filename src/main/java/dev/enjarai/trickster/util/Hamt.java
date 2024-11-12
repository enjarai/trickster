//https://gist.github.com/Alwinfy/b96bec5ed5def163b2c0a3ea3af670d8

package dev.enjarai.trickster.util;


import java.util.*;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import io.wispforest.endec.Endec;

public class Hamt<K, V> implements Iterable<Map.Entry<K, V>> {
    private static Hamt<?, ?> EMPTY = new Hamt<>(null);
    
    static <K> boolean equals(K left, K right) {
        return left.equals(right);
    }

    static <K> int hash(K key) {
        return key.hashCode();
    }
    // This is a simple HAMT, see https://github.com/python/cpython/blob/main/Python/hamt.c
    // The only difference is that "collision nodes" are LeafNodes, which can be either
    // SingleNodes or CollisionNodes (the former for better performance, since Java arrays aren't inline).
    //
    // This demo impl uses Hamt.equals() for testing keys. Probably want to swap that for iota equality.
    sealed interface HamtNode<K, V> {
        HamtNode<K, V> assoc(int hash, K key, V val);
        Optional<V> get(int hash, K key);
        HamtNode<K, V> dissoc(int hash, K key);
        int size();
    }

    // Array node: store children "densely" (when there are >16 children); size is the number of nonnull children
    static record ArrayNode<K, V>(int size, HamtNode<K, V>[] children) implements HamtNode<K, V> {
        @Override
        public HamtNode<K, V> assoc(int hash, K key, V val) {
            int next = hash >>> 5;
            hash &= 0x1f;
            var child = children[hash];
            if (child != null) {
                var newChild = child.assoc(next, key, val);
                if (newChild == child) {
                    return this;
                }
                var newChildren = Arrays.copyOf(children, children.length);
                newChildren[hash] = newChild;
                return new ArrayNode<>(size, newChildren);
            }
            var newChildren = Arrays.copyOf(children, children.length);
            newChildren[hash] = new SingleNode<>(next, key, val);
            return new ArrayNode<>(size + 1, newChildren);
        }
        @Override
        public Optional<V> get(int hash, K key) {
            int next = hash >>> 5;
            var child = children[hash & 0x1f];
            return child == null ? Optional.empty() : child.get(next, key);
        }
        @Override
        public HamtNode<K, V> dissoc(int hash, K key) {
            int next = hash >>> 5;
            hash &= 0x1f;
            var child = children[hash];
            if (child == null) {
                return this;
            }
            var newChild = child.dissoc(next, key);
            if (newChild == child) {
                return this;
            }
            // TODO: if nchildren = 16 && newChild == null, downgrade?
            if (size <= 16 && newChild == null) {
                int pop = 0, index = 0;
                @SuppressWarnings("unchecked")
                var newChildren = (HamtNode<K, V>[]) new HamtNode<?, ?>[size - 1];

                for (int i = 0; i < children.length; i++) {
                    if (i != hash && children[i] != null) {
                        pop |= 1 << i;
                        newChildren[index++] = children[i];
                    }
                }
                assert (size - 1 == index);
                return new HamNode<>(pop, newChildren);
            }
            var newChildren = Arrays.copyOf(children, children.length);
            newChildren[hash] = newChild;
            return new ArrayNode<>(size - (newChild == null ? 1 : 0), newChildren);
        }

        @Override
        public int size() {
            int count = 0;
            for (int i = 0; i < children.length; i++) {
                if (children[i] != null) {
                    count += children[i].size();
                }
            }
            return count;
        }

        @Override public String toString() { return "A[" + Arrays.toString(children) + "]"; }
    }

    // Array node: store children "sparsely" (<16 children); pop is a bitmap of the 32 children this can have
    static record HamNode<K, V>(int pop, HamtNode<K, V>[] children) implements HamtNode<K, V> {
        @Override
        public HamtNode<K, V> assoc(int hash, K key, V val) {
            int next = hash >>> 5;
            hash &= 0x1f;
            int index = indexOf(pop, hash);
            if (hasHash(pop, hash)) {
                var child = children[index];
                var newChild = child.assoc(next, key, val);
                if (child == newChild) {
                    return this;
                }
                var newChildren = Arrays.copyOf(children, children.length);
                newChildren[index] = newChild;
                return new HamNode<>(pop, newChildren);
            }
            if (children.length >= 15) {
                @SuppressWarnings("unchecked")
                var arrayEnts = (HamtNode<K, V>[]) new HamtNode<?, ?>[32];
                int work = pop, inputPos = 0;
                while (work != 0) {
                    int outputPos = Integer.numberOfTrailingZeros(work);
                    work &= work - 1; // remove lowest 1
                    arrayEnts[outputPos] = children[inputPos++];
                }
                arrayEnts[hash] = new SingleNode<>(next, key, val);
                return new ArrayNode<>(1 + inputPos, arrayEnts);
            }
            @SuppressWarnings("unchecked")
            var newChildren = (HamtNode<K, V>[]) new HamtNode<?, ?>[children.length + 1];
            System.arraycopy(children, 0, newChildren, 0, index);
            System.arraycopy(children, index, newChildren, index + 1, children.length - index);
            newChildren[index] = new SingleNode<>(next, key, val);
            return new HamNode<>(pop | 1 << hash, newChildren);
        }
        @Override
        public Optional<V> get(int hash, K key) {
            return hasHash(pop, hash & 0x1f) ? children[indexOf(pop, hash & 0x1f)].get(hash >>> 5, key) : Optional.empty();
        }
        @Override
        public HamtNode<K, V> dissoc(int hash, K key) {
            int next = hash >>> 5;
            hash &= 0x1f;
            if (!hasHash(pop, hash)) {
                return this;
            }
            int index = indexOf(pop, hash);
            var child = children[index];
            var newChild = child.dissoc(next, key);
            if (child == newChild) {
                return this;
            }
            if (newChild != null) {
                var newChildren = Arrays.copyOf(children, children.length);
                newChildren[index] = newChild;
                return new HamNode<>(pop, newChildren);
            }
            if (children.length == 1) {
                return null;
            }
            int newPop = pop & ~(1 << hash);
            if (children.length == 2) {
                int remainingHash = Integer.numberOfTrailingZeros(newPop);
                var childNode = children[indexOf(pop, remainingHash)];
                if (childNode instanceof LeafNode<K, V> ln) {
                    return ln.withNewHash(ln.tailHash() << 5 | remainingHash);
                }
            }
            @SuppressWarnings("unchecked")
            var newChildren = (HamtNode<K, V>[]) new HamtNode<?, ?>[children.length - 1];
            System.arraycopy(children, 0, newChildren, 0, index);
            System.arraycopy(children, index + 1, newChildren, index, children.length - index - 1);
            return new HamNode<>(newPop, newChildren);
        }

        @Override
        public int size() {
            int count = 0;
            for (int i = 0; i < children.length; i++) {
                count += children[i].size();
            }
            return count;
        }

        static boolean hasHash(int pop, int hash) {
            int offset = 1 << hash;
            return (pop & offset) != 0;
        }

        static int indexOf(int pop, int hash) {
            int offset = 1 << hash;
            return Integer.bitCount(pop & (offset - 1));
        }

        @Override public String toString() { return "H[" + Integer.toString(pop, 2) + ", " + Arrays.toString(children) + "]"; }
    }

    // Leaf node: stores data directly
    sealed interface LeafNode<K, V> extends HamtNode<K, V> {
        /** The "rest of the hash" that was leftover after reaching this point */
        int tailHash();

        /** add a key, val pair to this leaf node directly */
        HamtNode<K, V> doAssoc(K key, V val);

        /** Reconstruct this node with a new "tail hash" */
        LeafNode<K, V> withNewHash(int newHash);

        @Override
        default HamtNode<K, V> assoc(int hash, K key, V val) {
            if (hash == tailHash()) {
                return doAssoc(key, val);
            }
            return assocRecursive(hash, tailHash(), key, val);
        }

        default HamtNode<K, V> assocRecursive(int hash, int tailHash, K key, V val) {
            int nextHash = hash >>> 5;
            int nextTailHash = tailHash >>> 5;
            hash &= 0x1f;
            tailHash &= 0x1f;
            if (hash == tailHash) {
                @SuppressWarnings("unchecked")
                var child = (HamtNode<K, V>[]) new HamtNode<?, ?>[] {assocRecursive(nextHash, nextTailHash, key, val)};
                return new HamNode<>(1 << hash, child);
            }
            var existingNode = withNewHash(nextTailHash);
            var newNode = new SingleNode<>(nextHash, key, val);
            var left = hash < tailHash ? newNode : existingNode;
            var right = hash < tailHash ? existingNode : newNode;
            @SuppressWarnings("unchecked")
            var child = (HamtNode<K, V>[]) new HamtNode<?, ?>[] {left, right};
            return new HamNode<>(1 << hash | 1 << tailHash, child);
        }

        /** given an int [0..size()), return the key/val pair */
        Map.Entry<K, V> fetch(int index);
    }

    static record SingleNode<K, V>(@Override int tailHash, K key, V value) implements LeafNode<K, V> {
        @Override
        public SingleNode<K, V> withNewHash(int newHash) {
            return new SingleNode<>(newHash, key, value);
        }
        @Override
        public HamtNode<K, V> doAssoc(K key, V val) {
            return Hamt.equals(this.key, key) ? new SingleNode<>(tailHash, key, val) :
                    new CollisionNode<>(tailHash, new Object[] {this.key, this.value, key, val});
        }
        @Override
        public Optional<V> get(int hash, K key) {
            if (tailHash == hash && Hamt.equals(this.key, key)) {
                return Optional.of(value);
            }
            return Optional.empty();
        }
        @Override
        public HamtNode<K, V> dissoc(int hash, K key) {
            if (tailHash == hash && Hamt.equals(this.key, key)) {
                return null;
            }
            return this;
        }

        @Override public int size() { return 1; }
        @Override public Map.Entry<K, V> fetch(int index) { return new SimpleImmutableEntry<>(key, value); }
    }

    // storing keys and vals as adjacent objects :/
    // unsexy and untypeful but it is what it is
    static record CollisionNode<K, V>(@Override int tailHash, Object[] entries) implements LeafNode<K, V> {
        @Override
        public CollisionNode<K, V> withNewHash(int newHash) {
            return new CollisionNode<>(newHash, entries);
        }
        @Override
        public HamtNode<K, V> doAssoc(K key, V val) {
            for (int i = 0; i < entries.length; i += 2) {
                @SuppressWarnings("unchecked")
                var thisKey = (K) entries[i];
                if (Hamt.equals(thisKey, key)) {
                    var newVals = Arrays.copyOf(entries, entries.length);
                    newVals[i + 1] = val;
                    return new CollisionNode<>(tailHash, newVals);
                }
            }
            var newVals = Arrays.copyOf(entries, entries.length + 2);
            newVals[entries.length] = key;
            newVals[entries.length + 1] = val;
            return new CollisionNode<>(tailHash, newVals);
        }
        @Override
        public Optional<V> get(int hash, K key) {
            if (hash == tailHash) {
                for (int i = 0; i < entries.length; i+=2) {
                    @SuppressWarnings("unchecked")
                    var thisKey = (K) entries[i];
                    if (Hamt.equals(thisKey, key)) {
                        @SuppressWarnings("unchecked")
                        var value = (V) entries[i + 1];
                        return Optional.of(value);
                    }
                }
            }
            return Optional.empty();
        }
        @Override
        public HamtNode<K, V> dissoc(int hash, K key) {
            if (hash == tailHash) {
                for (int i = 0; i < entries.length; i+=2) {
                    @SuppressWarnings("unchecked")
                    var thisKey = (K) entries[i];
                    if (Hamt.equals(thisKey, key)) {
                        if (entries.length == 4) {
                            @SuppressWarnings("unchecked")
                            var otherKey = (K) entries[i ^ 2];
                            @SuppressWarnings("unchecked")
                            var otherVal = (V) entries[i ^ 3];
                            return new SingleNode<>(tailHash, otherKey, otherVal);
                        }
                        var newEntries = new Object[entries.length - 2];
                        System.arraycopy(entries, 0, newEntries, 0, i);
                        System.arraycopy(entries, i + 2, newEntries, i, entries.length - i - 2);
                        return new CollisionNode<>(tailHash, newEntries);
                    }
                }
            }
            return this;
        }

        @Override public int size() { return entries.length / 2; }
        @SuppressWarnings("unchecked")
        @Override public Map.Entry<K, V> fetch(int index) {
            return new SimpleImmutableEntry<>((K) entries[2 * index], (V) entries[2 * index + 1]);
        }
    }

    private HamtNode<K, V> root;

    private Hamt(HamtNode<K, V> root) {
        this.root = root;
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Hamt<K, V> empty() {
        return (Hamt<K, V>) EMPTY;
    }

    public static <K, V> Endec<Hamt<K, V>> endec(Endec<K> key, Endec<V> value) {
        return Endec.map(key, value).xmap(Hamt::fromMap, Hamt::asMap);
    }

    public Hamt<K, V> assoc(K key, V value) {
        return new Hamt<>(root != null ? root.assoc(Hamt.hash(key), key, value) : new SingleNode<>(Hamt.hash(key), key, value));
    }

    public Hamt<K, V> dissoc(K key) {
        return new Hamt<>(root == null ? null : root.dissoc(Hamt.hash(key), key));
    }

    public Optional<V> get(K key) {
        return root == null ? Optional.empty() : root.get(Hamt.hash(key), key);
    }

    public int size() {
        return root == null ? 0 : root.size();
    }

    public boolean isEmpty() {
        return !(size() > 0);
    }

    public Hamt<K, V> assocAll(Iterable<? extends Map.Entry<? extends K, ? extends V>> values) {
        var out = this;
        for (var entry : values) {
            out = out.assoc(entry.getKey(), entry.getValue());
        }
        return out;
    }

    public Map<K, V> asMap() {
        var map = new HashMap<K, V>();
        this.iterator().forEachRemaining(entry -> map.put(entry.getKey(), entry.getValue()));
        return map;
    }

    public Stream<Map.Entry<K, V>> stream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED), false);
    }

    public static<K, V> Hamt<K, V> ofIterable(Iterable<? extends Map.Entry<? extends K, ? extends V>> values) {
        return Hamt.<K, V>empty().assocAll(values);
    }

    public static<K, V> Hamt<K, V> fromMap(Map<? extends K, ? extends V> map) {
        return Hamt.ofIterable(map.entrySet());
    }

    HamtNode<K, V> root() { return root; }

    // iterator over a HAMT
    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        return new Iterator<Map.Entry<K, V>>() {
            private int depth = 0;
            @SuppressWarnings("unchecked")
            private HamtNode<K, V>[] nodeStack = (HamtNode<K, V>[])new HamtNode<?, ?>[15];
            private int[] progressStack = new int[15];
            private LeafNode<K, V> leafNode;
            private int leafProgress;

            {
                if (root == null) {
                    depth = -1;
                } else {
                    nodeStack[0] = root;
                    advance();
                }
            }
            @Override
            public boolean hasNext() {
                return leafProgress > 0 || depth >= 0;
            }

            public Map.Entry<K, V> next() {
                var value = leafNode.fetch(--leafProgress);
                if (leafProgress == 0) {
                    advance();
                }
                return value;
            }

            void advance() {
                loop: while (depth >= 0) switch (nodeStack[depth]) {
                    case ArrayNode<K, V> arr -> {
                        for (int i = progressStack[depth]; i < arr.children().length; i++) {
                            if (arr.children()[i] != null) {
                                progressStack[depth] = i + 1;
                                nodeStack[++depth] = arr.children()[i];
                                progressStack[depth] = 0;
                                continue loop;
                            }
                        }
                        depth--;
                    }
                    case HamNode<K, V> ham -> {
                        int i = progressStack[depth]++;
                        if (i < ham.children().length) {
                            nodeStack[++depth] = ham.children()[i];
                            progressStack[depth] = 0;
                            continue loop;
                        }
                        depth--;
                    }
                    case LeafNode<K, V> leaf -> {
                        leafNode = leaf;
                        leafProgress = leaf.size();
                        depth--;
                        return;
                    }
                }
            }
        };
    }
}
