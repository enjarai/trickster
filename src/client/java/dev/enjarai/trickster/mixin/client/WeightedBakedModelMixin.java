package dev.enjarai.trickster.mixin.client;

import net.fabricmc.fabric.api.renderer.v1.model.WrapperBakedModel;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.WeightedBakedModel;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(WeightedBakedModel.class)
public class WeightedBakedModelMixin implements WrapperBakedModel {
    @Shadow
    @Final
    private BakedModel defaultModel;

    @Override
    public @Nullable BakedModel getWrappedModel() {
        return this.defaultModel;
    }
}
