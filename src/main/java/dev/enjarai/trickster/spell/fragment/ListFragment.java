package dev.enjarai.trickster.spell.fragment;

import com.google.common.collect.ImmutableList;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.util.FuzzyUtils;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.execution.executor.FoldingSpellExecutor;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Stack;

public record ListFragment(List<Fragment> fragments) implements FoldableFragment {
    public static final StructEndec<ListFragment> ENDEC = StructEndecBuilder.of(
            Fragment.ENDEC.listOf().fieldOf("fragments", ListFragment::fragments),
            ListFragment::new
    );
    public static final ListFragment EMPTY = new ListFragment(ImmutableList.of());

    @Override
    public FragmentType<?> type() {
        return FragmentType.LIST;
    }

    @Override
    public Text asText() {
        var result = Text.literal("[");

        for (int i = 0; i < fragments().size(); i++) {
            var frag = fragments().get(i);
            if (i != 0) {
                result = result.append(", ");
            }
            result = result.append(frag.asFormattedText());
        }

        return result.append("]");
    }

    @Override
    public Fragment applyEphemeral() {
        return new ListFragment(fragments.stream().map(Fragment::applyEphemeral).toList());
    }

    @Override
    public int getWeight() {
        int weight = 16;

        for (Fragment fragment : fragments) {
            weight += fragment.getWeight();
        }

        return weight;
    }

    @Override
    public boolean fuzzyEquals(Fragment other) {
        if (other instanceof ListFragment that) {
            return FuzzyUtils.fuzzyEquals(this.fragments, that.fragments);
        }

        return false;
    }

    @Override
    public int fuzzyHash() {
        return FuzzyUtils.fuzzyHash(fragments);
    }

    @Override
    public FoldingSpellExecutor fold(SpellContext ctx, SpellPart executable, Fragment identity) {
        var keys = new Stack<Fragment>();
        var values = new Stack<Fragment>();

        for (int i = fragments.size() - 1; i >= 0; i--)
            keys.push(new NumberFragment(i));

        values.addAll(fragments.reversed());
        return new FoldingSpellExecutor(ctx, executable, identity, values, keys, this);
    }

    public ListFragment addRange(ListFragment other) throws BlunderException {
        return new ListFragment(ImmutableList.<Fragment>builder().addAll(fragments).addAll(other.fragments).build());
    }
}
