package dev.enjarai.trickster.spell.fragment;

import com.mojang.serialization.MapCodec;
import dev.enjarai.trickster.spell.Fragment;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public record TypeFragment(FragmentType<?> typeType) implements Fragment {
    public static final MapCodec<TypeFragment> CODEC = Identifier.CODEC.fieldOf("of_type")
            .xmap(identifier -> new TypeFragment(FragmentType.REGISTRY.get(identifier)),
                    typeFragment -> FragmentType.REGISTRY.getId(typeFragment.typeType));

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
