package dev.enjarai.trickster.spell;

import com.mojang.serialization.MapCodec;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;

public interface Fragment {
    MapCodec<Fragment> CODEC = FragmentType.REGISTRY.getCodec().dispatchMap(Fragment::type, FragmentType::codec);

    FragmentType<?> type();

    String asString();

    BooleanFragment asBoolean();
}
