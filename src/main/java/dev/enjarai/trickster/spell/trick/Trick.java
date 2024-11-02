package dev.enjarai.trickster.spell.trick;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.item.TrickyAccessoryItem;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.CantEditBlockBlunder;
import dev.enjarai.trickster.spell.blunder.IncorrectFragmentBlunder;
import dev.enjarai.trickster.spell.blunder.MissingFragmentBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mojang.datafixers.util.Either;

public abstract class Trick {
    public static final Identifier TRICK_RANDOM = Trickster.id("trick");

    protected final Pattern pattern;

    public Trick(Pattern pattern) {
        this.pattern = pattern;
    }

    public final Pattern getPattern() {
        return pattern;
    }

    public abstract Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException;

    @SuppressWarnings("unchecked")
	protected <T extends Fragment> T expectInput(List<Fragment> fragments, FragmentType<T> type, int index) throws BlunderException {
        if (fragments.size() <= index) {
            throw new MissingFragmentBlunder(this, index, type.getName());
        }

        var fragment = fragments.get(index);

        if (fragment.type() != type) {
            throw new IncorrectFragmentBlunder(this, index, type.getName(), fragment);
        }

        return (T) fragment;
    }

    @SuppressWarnings("unchecked")
	protected <T extends Fragment> T expectType(Fragment fragment, FragmentType<T> type) throws BlunderException {
        if (fragment.type() != type) {
            throw new IncorrectFragmentBlunder(this, -1, type.getName(), fragment);
        }

        return (T) fragment;
    }

    @SuppressWarnings("unchecked")
	protected <T extends Fragment> Optional<T> supposeInput(List<Fragment> fragments, FragmentType<T> type, int index) throws BlunderException {
        if (fragments.size() <= index) {
            return Optional.empty();
        }

        var fragment = fragments.get(index);

        if (fragment.type() != type) {
            throw new IncorrectFragmentBlunder(this, index, type.getName(), fragment);
        }

        return Optional.of((T) fragment);
    }

    protected Optional<Fragment> supposeInput(List<Fragment> fragments, int index) {
        if (fragments.size() <= index) {
            return Optional.empty();
        }

        return Optional.of(fragments.get(index));
    }

    protected <T1 extends Fragment, T2 extends Fragment> Optional<Either<T1, T2>> supposeEitherInput(List<Fragment> fragments, FragmentType<T1> primary, FragmentType<T2> alternative, int index) {
        var input = supposeInput(fragments, index);
        var r1 = input.flatMap(fragment -> supposeType(fragment, primary));

        if (r1.isPresent())
            return Optional.of(Either.left(r1.get()));

        var r2 = input.flatMap(fragment -> supposeType(fragment, alternative));

        if (r2.isPresent())
            return Optional.of(Either.right(r2.get()));

        return Optional.empty();
    }

    protected <T1 extends Fragment, T2 extends Fragment> Either<T1, T2> expectEitherInput(List<Fragment> fragments, FragmentType<T1> primary, FragmentType<T2> alternative, int index) throws BlunderException {
        var input = supposeInput(fragments, index);
        var expected = Text.literal("Either of ").append(primary.getName()).append(" or ").append(alternative.getName());
        return supposeEitherInput(fragments, primary, alternative, index)
            .orElseThrow(() -> input.
                    <BlunderException>map(fragment -> new IncorrectFragmentBlunder(this, index, expected, fragment))
                    .orElse(new MissingFragmentBlunder(this, index, expected)));
    }

    @SuppressWarnings("unchecked")
	protected <T extends Fragment> Optional<T> supposeType(Fragment fragment, FragmentType<T> type) {
        if (fragment.type() != type) {
            return Optional.empty();
        }

        return Optional.of((T) fragment);
    }

    @SuppressWarnings("unchecked")
	protected <T extends Fragment> T expectInput(List<Fragment> fragments, Class<T> type, int index) throws BlunderException {
        if (fragments.size() <= index) {
            throw new MissingFragmentBlunder(this, index, Text.of(type.getSimpleName()));
        }

        var fragment = fragments.get(index);

        if (!type.isInstance(fragment)) {
            throw new IncorrectFragmentBlunder(this, index, Text.literal(type.getSimpleName()), fragment);
        }

        return (T) fragment;
    }

    @SuppressWarnings("unchecked")
	protected <T extends Fragment> T expectType(Fragment fragment, Class<T> type, int index) throws BlunderException {
        if (!type.isInstance(fragment)) {
            throw new IncorrectFragmentBlunder(this, index, Text.literal(type.getSimpleName()), fragment);
        }

        return (T) fragment;
    }

    protected Fragment expectInput(List<Fragment> fragments, int index) throws BlunderException {
        if (fragments.size() <= index) {
            throw new MissingFragmentBlunder(this, index, Text.of("any"));
        }

        return fragments.get(index);
    }

    @SafeVarargs
    protected final <T extends Fragment> List<T> expectVariadic(List<Fragment> fragments, int index, Class<T>... types) throws BlunderException {
        var result = new ArrayList<T>();
        int offset = 0;

        for (var fragment : fragments.subList(index, fragments.size())) {
            result.add(expectType(fragment, types[offset % types.length], index + offset));
            offset++;
        }

        if (offset % types.length != 0) {
            throw new MissingFragmentBlunder(this, offset, Text.of(types[offset % types.length].getSimpleName()));
        }

        return result;
    }

    protected void expectCanBuild(SpellContext ctx, BlockPos... positions) {
        if (ctx.source().getPlayer().isEmpty()) {
            return;
        }

        var player = ctx.source().getPlayer().get();

        if (player.interactionManager.getGameMode().isBlockBreakingRestricted()) {
            throw new CantEditBlockBlunder(this, positions[0]);
        }

        for (var pos : positions) {
            if (!player.canModifyAt(ctx.source().getWorld(), pos)) {
                throw new CantEditBlockBlunder(this, pos);
            }
        }
    }

    protected void tryWard(SpellContext ctx, Entity target, List<Fragment> fragments) throws BlunderException {
        if (target instanceof ServerPlayerEntity player) {
            TrickyAccessoryItem.tryWard(ctx, player, this, fragments);
        }
    }

    public MutableText getName() {
        var id = Tricks.REGISTRY.getId(this);

        if (id == null) {
            return Text.literal("Unregistered");
        }

        return Text.literal("").append(
                Text.translatable(Trickster.MOD_ID + ".trick." + id.getNamespace() + "." + id.getPath())
                        .withColor(FragmentType.PATTERN.color().getAsInt()));
    }
}
