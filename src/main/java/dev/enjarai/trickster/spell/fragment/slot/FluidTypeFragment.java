package dev.enjarai.trickster.spell.fragment.slot;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public record FluidTypeFragment(Fluid fluid) implements Fragment, ResourceVariantFragment<FluidVariant> {
    public static final StructEndec<FluidTypeFragment> ENDEC = StructEndecBuilder.of(
            MinecraftEndecs.ofRegistry(Registries.FLUID).fieldOf("fluid", FluidTypeFragment::fluid),
            FluidTypeFragment::new
    );

    @Override
    public FragmentType<?> type() {
        return FragmentType.FLUID_TYPE;
    }

    @Override
    public Text asText() {
        Block fluidBlock = fluid.getDefaultState().getBlockState().getBlock();

        if (fluidBlock == Blocks.AIR) {
            // Some non-placeable fluids use air as their fluid block, in that case infer translation key from the fluid id.
            return Text.translatable(Util.createTranslationKey("block", Registries.FLUID.getId(fluid)));
        } else {
            return fluidBlock.getName();
        }
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
        return resource.isOf(fluid);
    }
}
