package dev.enjarai.trickster.render;

import dev.enjarai.trickster.cca.ModEntityCumponents;
import dev.enjarai.trickster.fleck.*;
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

        player.getComponent(ModEntityCumponents.FLECKS).getFlecks().forEach((id, pair) -> {
            var fleck = pair.getFirst();
            var fleckId = FleckType.REGISTRY.getId(fleck.type());
            // Not using a type parameter here SHOULD be safe.
            //noinspection rawtypes
            FleckRenderer renderer = FleckRenderer.REGISTRY.get(fleckId);
            if (renderer == null) {
                throw new IllegalStateException("Missing renderer for fleck " + fleckId + "!");
            }

            colorsRandom.setSeed(id);
            var color = ColorHelper.Argb.fromFloats(1f, colorsRandom.nextFloat(), colorsRandom.nextFloat(), colorsRandom.nextFloat());

            //noinspection unchecked
            renderer.render(
                    fleck,
                    worldRenderContext, worldRenderContext.world(), worldRenderContext.tickCounter().getTickDelta(false),
                    worldRenderContext.matrixStack(), worldRenderContext.consumers(), color
            );
        });
    }
}
