package dev.enjarai.trickster.render;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.fleck.*;
import dev.enjarai.trickster.render.fleck.*;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;

public class FlecksRenderer {
    public static void render(WorldRenderContext worldRenderContext) {
        var player = MinecraftClient.getInstance().player;
        if (player == null) {
            return;
        }

        player.getComponent(ModEntityComponents.FLECKS).getRenderFlecks().forEach(flecks -> {
            var fleck = flecks.current();
            var lastFleck = flecks.old();

            if (lastFleck != null && lastFleck.type() != fleck.type()) {
                lastFleck = null;
            }

            var fleckId = FleckType.REGISTRY.getId(fleck.type());
            // Not using a type parameter here SHOULD be safe.
            //noinspection rawtypes
            FleckRenderer renderer = FleckRenderer.REGISTRY.get(fleckId);
            if (renderer == null) {
                throw new IllegalStateException("Missing renderer for fleck " + fleckId + "!");
            }

            int color = 0;
            if (fleck instanceof PaintableFleck paintable) {
                color = paintable.getColor();
            }
            //noinspection unchecked
            renderer.render(
                    fleck,
                    lastFleck,
                    worldRenderContext, worldRenderContext.world(), worldRenderContext.tickCounter().getTickDelta(true),
                    worldRenderContext.matrixStack(), worldRenderContext.consumers(), color
            );
        });
    }
}
