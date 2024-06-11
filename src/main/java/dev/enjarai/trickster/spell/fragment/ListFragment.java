package dev.enjarai.trickster.spell.fragment;

import com.mojang.serialization.MapCodec;
import dev.enjarai.trickster.spell.Fragment;

import java.util.List;
import java.util.stream.Collectors;

public record ListFragment(List<Fragment> fragments) implements Fragment {
    public static final MapCodec<ListFragment> CODEC =
            Fragment.CODEC.codec().listOf().fieldOf("fragments").xmap(ListFragment::new, ListFragment::fragments);

    @Override
    public FragmentType<?> type() {
        return FragmentType.LIST;
    }

    @Override
    public String asString() {
        return "[" + fragments.stream().map(Fragment::asString).collect(Collectors.joining(", ")) + "]";
    }

    @Override
    public BooleanFragment asBoolean() {
        return new BooleanFragment(!fragments.isEmpty());
    }
}
