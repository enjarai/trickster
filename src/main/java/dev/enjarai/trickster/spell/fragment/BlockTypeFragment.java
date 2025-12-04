package dev.enjarai.trickster.spell.fragment;

import dev.enjarai.trickster.mixin.accessor.FluidBlockAccessor;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.slot.ResourceVariantFragment;
import dev.enjarai.trickster.spell.fragment.slot.VariantType;
import dev.enjarai.trickster.spell.trick.Trick;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

public record BlockTypeFragment(Block block) implements Fragment, ResourceVariantFragment<FluidVariant> {
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
    public int getWeight() {
        return 16;
    }

    @Override
    public VariantType<FluidVariant> variantType() {
        return VariantType.FLUID;
    }

    @Override
    public boolean resourceMatches(Trick<?> trick, SpellContext ctx, FluidVariant resource) {
        return block instanceof FluidBlockAccessor fluid && resource.getFluid().matchesType(fluid.getFluid());
    }
}
