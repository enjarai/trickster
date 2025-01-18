package dev.enjarai.trickster.spell.fragment;

import dev.enjarai.trickster.spell.Fragment;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

public record ItemTypeFragment(Item item) implements Fragment {
    public static final StructEndec<ItemTypeFragment> ENDEC = StructEndecBuilder.of(
            MinecraftEndecs.ofRegistry(Registries.ITEM).fieldOf("item", ItemTypeFragment::item),
            ItemTypeFragment::new
    );

    @Override
    public FragmentType<?> type() {
        return FragmentType.ITEM_TYPE;
    }

    @Override
    public Text asText() {
        return item.getName();
    }

    @Override
    public int getWeight() {
        return 16;
    }
}
