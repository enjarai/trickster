package dev.enjarai.trickster.render;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.render.fleck.*;
import dev.enjarai.trickster.spell.fleck.FleckType;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.util.math.random.Random;

public class FlecksRenderer {
    private static final Random colorsRandom = new LocalRandom(0xba115);

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

            colorsRandom.setSeed(flecks.id());
            var color = ColorHelper.Argb.fromFloats(1f, colorsRandom.nextFloat(), colorsRandom.nextFloat(), colorsRandom.nextFloat());

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
