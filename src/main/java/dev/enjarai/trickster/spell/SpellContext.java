package dev.enjarai.trickster.spell;

import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.ExecutionLimitReachedBlunder;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector3d;

import java.util.*;

public abstract class SpellContext {
    public static final int MAX_RECURSION_DEPTH = 256;

    private final Deque<List<Fragment>> partGlyphStack = new ArrayDeque<>();
    private int recursions = 0;
    private boolean destructive = false;
    private boolean hasAffectedWorld = false;
    private final Deque<Integer> stacktrace = new ArrayDeque<>();

    public void pushPartGlyph(List<Fragment> fragments) throws BlunderException {
        partGlyphStack.push(fragments);
        recursions++;
        if (recursions > MAX_RECURSION_DEPTH) {
            throw new ExecutionLimitReachedBlunder();
        }
    }

    public void popPartGlyph() {
        partGlyphStack.pop();
//        recursions--; // For now, we'll actually have a maximum of 256 "function" calls in one spell execution, period.
    }

    public List<Fragment> peekPartGlyph() {
        var result = partGlyphStack.peek();
        if (result != null) {
            return result;
        }
        return List.of();
    }

    /**
     * >0: Actual index
     * -1: Glyph call
     * -2: Pattern call
     */
    public void pushStackTrace(int i) {
        stacktrace.push(i);
    }

    public void popStackTrace() {
        stacktrace.pop();
    }

    public Text formatStackTrace() {
        MutableText result = null;

        for (var i : stacktrace.reversed()) {
            if (result == null) {
                result = Text.literal("");
            } else {
                result = result.append(":");
            }

            result = result.append(switch (i) {
                case -1 -> ">";
                case -2 -> "#";
                default -> "" + i;
            });
        }

        return result == null ? Text.of("") : result;
    }

    public Optional<ServerPlayerEntity> getPlayer() {
        return Optional.empty();
    }

    public Optional<Entity> getCaster() {
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

    public abstract Fragment getCrowMind();

    public abstract void setCrowMind(Fragment fragment);

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
