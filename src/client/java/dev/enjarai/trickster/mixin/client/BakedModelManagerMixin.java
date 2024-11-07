package dev.enjarai.trickster.mixin.client;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.render.MultiSpellCircleBlockEntityRenderer;
import dev.enjarai.trickster.render.ScrollShelfBlockEntityRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(BakedModelManager.class)
public class BakedModelManagerMixin {
    @Final
    @Shadow @Mutable
    private static Map<Identifier, Identifier> LAYERS_TO_LOADERS;

    @Inject(
            method = "<clinit>",
            at = @At("TAIL")
    )
    private static void appendToAtlases(CallbackInfo ci) {
        LAYERS_TO_LOADERS = new HashMap<>(LAYERS_TO_LOADERS);
        LAYERS_TO_LOADERS.put(ScrollShelfBlockEntityRenderer.ATLAS_ID, Trickster.id("scroll_shelf"));
        LAYERS_TO_LOADERS.put(MultiSpellCircleBlockEntityRenderer.ATLAS_ID, Trickster.id("spell_cores"));
    }
}
