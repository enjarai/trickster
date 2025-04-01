package dev.enjarai.trickster.spell.trick;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.advancement.criterion.ModCriteria;
import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.item.component.FragmentComponent;
import dev.enjarai.trickster.spell.EvaluationResult;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.*;
import dev.enjarai.trickster.spell.execution.source.PlayerSpellSource;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.type.SimpleArgType;
import dev.enjarai.trickster.spell.type.TypeListArgType;
import dev.enjarai.trickster.spell.type.TypeMapArgType;
import dev.enjarai.trickster.spell.type.ClassListArgType;
import dev.enjarai.trickster.spell.type.ClassMapArgType;
import dev.enjarai.trickster.spell.type.ClassVariadicArgType;
import dev.enjarai.trickster.spell.type.TypeVariadicArgType;
import io.vavr.collection.HashMap;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class Trick<T extends Trick<T>> {
    public static final Identifier TRICK_RANDOM = Trickster.id("trick");

    protected static final SimpleArgType<Fragment> ANY = simple(Fragment.class);
    protected static final ClassVariadicArgType<Fragment> ANY_VARIADIC = variadic(Fragment.class);

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

    protected static <T extends Fragment> SimpleArgType<T> simple(Class<T> type) {
        return new SimpleArgType<T>(type);
    }

    @SafeVarargs
    protected static <T extends Fragment> ClassVariadicArgType<T> variadic(Class<T>... types) {
        return new ClassVariadicArgType<>(types);
    }

    @SafeVarargs
    protected static <T extends Fragment> TypeVariadicArgType<T> variadic(FragmentType<T>... types) {
        return new TypeVariadicArgType<>(types);
    }

    @SafeVarargs
    protected static <T extends Fragment> ClassListArgType<T> list(Class<T>... types) {
        return new ClassListArgType<>(types);
    }

    @SafeVarargs
    protected static <T extends Fragment> TypeListArgType<T> list(FragmentType<T>... types) {
        return new TypeListArgType<>(types);
    }

    protected static <K extends Fragment, V extends Fragment> ClassMapArgType<K, V> map(Class<K> keyType, Class<V> valueType) {
        return new ClassMapArgType<>(keyType, valueType);
    }

    protected static <K extends Fragment, V extends Fragment> TypeMapArgType<K, V> map(FragmentType<K> keyType, FragmentType<V> valueType) {
        return new TypeMapArgType<>(keyType, valueType);
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
            // blanketcon security measures
            if (ctx.source() instanceof PlayerSpellSource
                    && !Trickster.getArea(ctx.source().getWorld()).contains(ctx.source().getWorld(), pos.toCenterPos())) {
                throw new BlanketConOutOfBoundsBlunder(this, pos);
            }

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

    protected void tryWard(SpellContext triggerCtx, Entity target, List<Fragment> fragments) throws BlunderException {
        if (target instanceof ServerPlayerEntity player) {
            var triggerCaster = triggerCtx.source().getCaster();

            if (triggerCaster.map(c -> c.equals(player)).orElse(false)) {
                return;
            }

            ModCriteria.TRIGGER_WARD.trigger(player);

            var sourceFragment = triggerCaster
                    .<Fragment>map(EntityFragment::from)
                    .orElse(new VectorFragment(triggerCtx.source().getPos()));
            var charmMap = FragmentComponent.getUserMergedMap(player, "charm", HashMap::empty);
            var spell = charmMap.get(getPattern());
            var caster = ModEntityComponents.CASTER.get(player);
            spell.peek(s -> caster.queueSpellAndCast(s, List.of(sourceFragment, new ListFragment(fragments)), Optional.empty()));
        }
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
