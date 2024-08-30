package dev.enjarai.trickster.spell.fragment;

import com.google.common.collect.ImmutableList;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.blunder.IncorrectFragmentBlunder;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public record ListFragment(List<Fragment> fragments) implements Fragment {
    public static final StructEndec<ListFragment> ENDEC = StructEndecBuilder.of(
            Fragment.ENDEC.listOf().fieldOf("fragments", ListFragment::fragments),
            ListFragment::new
    );

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
    public boolean asBoolean() {
        return true;
    }

    public ListFragment addRange(ListFragment other) throws BlunderException {
        return new ListFragment(ImmutableList.<Fragment>builder().addAll(fragments).addAll(other.fragments).build());
    }

    public List<Integer> sanitizeAddress(Trick source) {
        var sanitizedAddress = new ArrayList<Integer>();

        for (Fragment fragment : this.fragments()) {
            if (fragment instanceof NumberFragment index && index.isInteger()) {
                sanitizedAddress.add((int) index.number());
            } else {
                throw new IncorrectFragmentBlunder(
                        source,
                        1,
                        Text.translatable(Trickster.MOD_ID + ".fragment." + Trickster.MOD_ID + "." + "integer_list"),
                        this);
            }
        }

        return sanitizedAddress;
    }
}
