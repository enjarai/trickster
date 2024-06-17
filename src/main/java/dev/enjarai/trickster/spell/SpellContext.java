package dev.enjarai.trickster.spell;

import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.RecursionLimitReachedBlunder;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector3d;

import java.util.*;

public abstract class SpellContext {
    public static final int MAX_RECURSION_DEPTH = 1000;

    private final Deque<List<Fragment>> partGlyphStack = new ArrayDeque<>();
    private int recursions = 0;
    private boolean destructive = false;
    private boolean hasAffectedWorld = false;

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
        return Optional.empty();
    }

    public Optional<ItemStack> getOtherHandSpellStack() {
        return Optional.empty();
    }

    public abstract Vector3d getPos();

    public BlockPos getBlockPos() {
        var pos = getPos();
        return new BlockPos((int) pos.x, (int) pos.y, (int) pos.z);
    }

    public abstract ServerWorld getWorld();

    public boolean isDestructive() {
        return destructive;
    }

    public SpellContext setDestructive() {
        destructive = true;
        return this;
    }

    public void setWorldAffected() {
        hasAffectedWorld = true;
    }

    public boolean hasAffectedWorld() {
        return hasAffectedWorld;
    }
}
