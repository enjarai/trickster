package dev.enjarai.trickster.render;

import dev.enjarai.trickster.cca.ModWorldComponents;
import dev.enjarai.trickster.render.ward.WardRenderer;
import dev.enjarai.trickster.spell.ward.Ward;
import dev.enjarai.trickster.spell.ward.WardType;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;

public class WardsRenderer {
    public static void render(WorldRenderContext worldRenderContext) {
        var player = MinecraftClient.getInstance().player;
        if (player == null) {
            return;
        }

        if (true) { //TODO: check if player is wearing a monocle
            worldRenderContext.world().getComponent(ModWorldComponents.WARD_MANAGER).getRenderWards().forEach(ward -> {
                var wardId = WardType.REGISTRY.getId(ward.type());
                @SuppressWarnings("rawtypes") // Not using a type parameter here SHOULD be safe
                WardRenderer renderer = WardRenderer.REGISTRY.get(wardId);
                if (renderer == null) {
                    throw new IllegalStateException("Missing renderer for ward " + wardId + "!");
                }

                renderer.render(
                        ward,
                        (Ward) null,
                        worldRenderContext,
                        worldRenderContext.matrixStack(),
                        worldRenderContext.consumers(),
                        worldRenderContext.tickCounter().getTickDelta(true)
                );
            });
        }
    }
}
