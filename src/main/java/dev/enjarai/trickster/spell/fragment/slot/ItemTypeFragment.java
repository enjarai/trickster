package dev.enjarai.trickster.spell.fragment.slot;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

public record ItemTypeFragment(Item item) implements Fragment, ResourceVariantFragment<ItemVariant> {
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

    @Override
    public VariantType<ItemVariant> variantType() {
        return VariantType.ITEM;
    }

    @Override
    public boolean slotContains(Trick<?> trick, SpellContext ctx, SlotFragment slotFragment) {
        return slotFragment.getResource(trick, ctx, variantType()).getItem() == item;
    }
}
