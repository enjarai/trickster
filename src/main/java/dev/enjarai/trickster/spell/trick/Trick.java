package dev.enjarai.trickster.spell.trick;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.cca.ModWorldComponents;
import dev.enjarai.trickster.spell.EvaluationResult;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlockedByWardBlunder;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.CantEditBlockBlunder;
import dev.enjarai.trickster.spell.blunder.InvalidInputsBlunder;
import dev.enjarai.trickster.spell.blunder.NotLoadedBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.type.*;
import dev.enjarai.trickster.spell.ward.action.Action;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class Trick<T extends Trick<T>> {
    public static final Identifier TRICK_RANDOM = Trickster.id("trick");

    protected final Pattern pattern;
    private final List<Signature<T>> handlers;

    public Trick(Pattern pattern, List<Signature<T>> handlers) {
        this.pattern = pattern;
        this.handlers = handlers;
    }

    public Trick(Pattern pattern, Signature<T> primary) {
        this(pattern);
        this.handlers.add(primary);
    }

    public Trick(Pattern pattern) {
        this(pattern, new ArrayList<>());
    }

    public final Pattern getPattern() {
        return pattern;
    }

    public Trick<T> overload(Signature<T> signature) {
        this.handlers.add(signature);
        return this;
    }

    @SuppressWarnings("unchecked")
    public EvaluationResult activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        for (int i = handlers.size() - 1; i >= 0; i--) {
            var handler = handlers.get(i);

            if (handler.match(fragments)) {
                return handler.run((T) this, ctx, fragments);
            }
        }

        throw new InvalidInputsBlunder(this, fragments);
    }

    protected void expectCanBuild(SpellContext ctx, BlockPos... positions) {
        if (ctx.source().getPlayer().isEmpty()) {
            return;
        }

        var player = ctx.source().getPlayer().get();

        if (player.interactionManager.getGameMode().isBlockBreakingRestricted()) {
            throw new CantEditBlockBlunder(this, positions[0]);
        }

        expectLoaded(ctx, positions);
        for (var pos : positions) {
            if (!player.canModifyAt(ctx.source().getWorld(), pos)) {
                throw new CantEditBlockBlunder(this, pos);
            }
        }
    }

    protected void expectLoaded(SpellContext ctx, BlockPos... positions) {
        for (var pos : positions) {
            if (!ctx.source().getWorld().isChunkLoaded(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()))) {
                throw new NotLoadedBlunder(this, pos);
            }
        }
    }

    protected void checkWard(SpellContext ctx, Action<?> action) {
        var wards = ModWorldComponents.WARD_MANAGER.get(ctx.source().getWorld());
        if (wards.shouldCancel(action)) {
            throw new BlockedByWardBlunder(this);
        }
    }

    public @Nullable Set<UUID> restricted() {
        return null;
    }

    public MutableText getName() {
        var id = Tricks.REGISTRY.getId(this);

        if (id == null) {
            return Text.literal("Unregistered");
        }

        return Text.literal("").append(
                Text.translatable(Trickster.MOD_ID + ".trick." + id.getNamespace() + "." + id.getPath())
                        .withColor(FragmentType.PATTERN.color().getAsInt())
        );
    }

    public List<Signature<T>> getSignatures() {
        return handlers;
    }
}
