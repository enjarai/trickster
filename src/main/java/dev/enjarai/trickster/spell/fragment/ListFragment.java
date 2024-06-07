package dev.enjarai.trickster.spell.fragment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.enjarai.trickster.spell.Fragment;

import java.util.List;

public record ListFragment(List<Fragment> fragments) implements Fragment {
    public static final MapCodec<ListFragment> CODEC =
            Fragment.CODEC.codec().listOf().fieldOf("fragments").xmap(ListFragment::new, ListFragment::fragments);

    @Override
    public FragmentType<?> type() {
        return FragmentType.LIST;
    }
}
