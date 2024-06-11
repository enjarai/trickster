package dev.enjarai.trickster.spell.fragment;

import com.mojang.serialization.MapCodec;
import dev.enjarai.trickster.spell.Fragment;
import net.minecraft.text.Text;

import java.util.List;
import java.util.stream.Collectors;

public record ListFragment(List<Fragment> fragments) implements Fragment {
    public static final MapCodec<ListFragment> CODEC =
            Fragment.CODEC.get().codec().listOf().fieldOf("fragments").xmap(ListFragment::new, ListFragment::fragments);

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
            result = result.append(frag.asText());
        }

        return result.append("]");
    }

    @Override
    public BooleanFragment asBoolean() {
        return new BooleanFragment(!fragments.isEmpty());
    }
}
