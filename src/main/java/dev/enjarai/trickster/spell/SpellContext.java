package dev.enjarai.trickster.spell;

import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.RecursionLimitReachedBlunder;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SpellContext {
    public static final int MAX_RECURSION_DEPTH = 1000;

    @Nullable
    private final ServerPlayerEntity player;
    private final Deque<List<Fragment>> partGlyphStack = new ArrayDeque<>();
    private int recursions = 0;

    public SpellContext(@Nullable ServerPlayerEntity player) {
        this.player = player;
    }

    public void pushPartGlyph(List<Fragment> fragments) throws BlunderException {
        partGlyphStack.push(fragments);
        recursions++;
        if (recursions > MAX_RECURSION_DEPTH) {
            throw new RecursionLimitReachedBlunder();
        }
    }

    public void popPartGlyph() {
        partGlyphStack.pop();
        recursions--;
    }

    public List<Fragment> peekPartGlyph() {
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
