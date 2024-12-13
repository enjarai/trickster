package dev.enjarai.trickster.spell.fragment;

import dev.enjarai.trickster.spell.Fragment;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

public record BlockTypeFragment(Block block) implements Fragment {
    public static final StructEndec<BlockTypeFragment> ENDEC = StructEndecBuilder.of(
            MinecraftEndecs.ofRegistry(Registries.BLOCK).fieldOf("block", BlockTypeFragment::block),
            BlockTypeFragment::new
    );

    @Override
    public FragmentType<?> type() {
        return FragmentType.BLOCK_TYPE;
    }

    @Override
    public Text asText() {
        return block.getName();
    }

    @Override
    public boolean asBoolean() {
        return true;
    }

    @Override
    public int getWeight() {
        return 16;
    }
}
