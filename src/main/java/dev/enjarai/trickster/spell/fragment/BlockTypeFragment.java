package dev.enjarai.trickster.spell.fragment;

import com.mojang.serialization.MapCodec;
import dev.enjarai.trickster.spell.Fragment;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

public record BlockTypeFragment(Block block) implements Fragment {
    public static final MapCodec<BlockTypeFragment> CODEC = Registries.BLOCK.getCodec()
            .xmap(BlockTypeFragment::new, BlockTypeFragment::block).fieldOf("block");

    @Override
    public FragmentType<?> type() {
        return FragmentType.BLOCK_TYPE;
    }

    @Override
    public Text asText() {
        return block.getName();
    }

    @Override
    public BooleanFragment asBoolean() {
        return BooleanFragment.TRUE;
    }
}
