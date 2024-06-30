package dev.enjarai.trickster.spell.fragment;

import com.mojang.serialization.MapCodec;
import dev.enjarai.trickster.spell.Fragment;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

public record ItemTypeFragment(Item item) implements Fragment {
    public static final MapCodec<ItemTypeFragment> CODEC = Registries.ITEM.getCodec()
            .xmap(ItemTypeFragment::new, ItemTypeFragment::item).fieldOf("item");

    @Override
    public FragmentType<?> type() {
        return FragmentType.ITEM_TYPE;
    }

    @Override
    public Text asText() {
        return item.getName();
    }

    @Override
    public BooleanFragment asBoolean() {
        return BooleanFragment.TRUE;
    }
}
