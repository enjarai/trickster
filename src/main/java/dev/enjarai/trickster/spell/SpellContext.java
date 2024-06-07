package dev.enjarai.trickster.spell;

import java.util.*;

public class SpellContext {
    private final Deque<List<Optional<Fragment>>> partGlyphStack = new ArrayDeque<>();

    public void pushPartGlyph(List<Optional<Fragment>> fragments) {
        partGlyphStack.push(fragments);
    }

    public void popPartGlyph() {
        partGlyphStack.pop();
    }

    public List<Optional<Fragment>> peekPartGlyph() {
        var result = partGlyphStack.peek();
        if (result != null) {
            return result;
        }
        return List.of();
    }
}
