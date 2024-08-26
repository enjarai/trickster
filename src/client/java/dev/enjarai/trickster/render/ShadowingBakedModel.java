package dev.enjarai.trickster.render;

import dev.enjarai.trickster.cca.ModChunkCumponents;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.WrapperBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

import java.util.function.Supplier;

public class ShadowingBakedModel extends ForwardingBakedModel {
    public ShadowingBakedModel(BakedModel original) {
        wrapped = original;
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
        var chunk = MinecraftClient.getInstance().world.getChunk(pos);
        var component = ModChunkCumponents.SHADOW_DISGUISE_MAP.get(chunk);
        var disguise = component.getFunnyState(pos);
        if (disguise != null) {
            var model = MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(disguise);
            model = WrapperBakedModel.unwrap(model);
            model.emitBlockQuads(blockView, state, pos, randomSupplier, context);
            return;
        }

        super.emitBlockQuads(blockView, state, pos, randomSupplier, context);
    }
}
