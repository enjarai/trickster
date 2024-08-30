package dev.enjarai.trickster.spell.fragment;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.spell.Fragment;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.text.Text;

public record TypeFragment(FragmentType<?> typeType) implements Fragment {
    public static final StructEndec<TypeFragment> ENDEC = EndecTomfoolery.lazy(() -> StructEndecBuilder.of(
            MinecraftEndecs.ofRegistry(FragmentType.REGISTRY).fieldOf("of_type", TypeFragment::typeType),
            TypeFragment::new
    ));

    @Override
    public FragmentType<?> type() {
        return FragmentType.TYPE;
    }

    @Override
    public Text asText() {
        return typeType.getName();
    }

    @Override
    public boolean asBoolean() {
        return true;
    }
}
