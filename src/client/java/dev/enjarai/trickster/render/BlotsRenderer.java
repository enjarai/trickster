package dev.enjarai.trickster.render;

import dev.enjarai.trickster.cca.ModEntityComponents;


import dev.enjarai.trickster.render.blot.BlotRenderer;
import dev.enjarai.trickster.spell.blot.BlotType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.util.math.random.Random;

public class BlotsRenderer {
    private static final Random colorsRandom = new LocalRandom(0xba115);

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        var player = MinecraftClient.getInstance().player;
        if (player == null) {
            return;
        }

        player.getComponent(ModEntityComponents.BLOTS).getRenderBlots().forEach(blots -> {
            var blot = blots.current();
            var lastBlot = blots.old();

            if (lastBlot != null && lastBlot.type() != blot.type()) {
                lastBlot = null;
            }

            var blotId = BlotType.REGISTRY.getId(blot.type());
            // Not using a type parameter here SHOULD be safe.
            //noinspection rawtypes
            BlotRenderer renderer = BlotRenderer.REGISTRY.get(blotId);
            if (renderer == null) {
                throw new IllegalStateException("Missing renderer for blot " + blotId + "!");
            }

            colorsRandom.setSeed(blots.id());
            var color = ColorHelper.Argb.fromFloats(1f, colorsRandom.nextFloat(), colorsRandom.nextFloat(), colorsRandom.nextFloat());

            //noinspection unchecked
            renderer.render(
                    blot,
                    lastBlot,
                    context, tickCounter,
                    color
            );
        });
    }
}
