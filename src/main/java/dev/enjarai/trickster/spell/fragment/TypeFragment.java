package dev.enjarai.trickster.spell.fragment;

import com.mojang.serialization.MapCodec;
import dev.enjarai.trickster.spell.Fragment;
import net.minecraft.text.Text;

public record TypeFragment(FragmentType<?> typeType) implements Fragment {
    public static final MapCodec<TypeFragment> CODEC = FragmentType.REGISTRY.getCodec()
            .fieldOf("type").xmap(TypeFragment::new, TypeFragment::typeType);

    @Override
    public FragmentType<?> type() {
        return FragmentType.TYPE;
    }

    @Override
    public Text asText() {
        return typeType.getName();
    }

    @Override
    public BooleanFragment asBoolean() {
        return BooleanFragment.TRUE;
    }
}
