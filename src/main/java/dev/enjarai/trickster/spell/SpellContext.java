package dev.enjarai.trickster.spell;

import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SpellContext {
    @Nullable
    private final ServerPlayerEntity player;
    private final Deque<List<Optional<Fragment>>> partGlyphStack = new ArrayDeque<>();

    public SpellContext(@Nullable ServerPlayerEntity player) {
        this.player = player;
    }

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

    public Optional<ServerPlayerEntity> getPlayer() {
        return Optional.ofNullable(player);
    }
}
