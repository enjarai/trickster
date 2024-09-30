package dev.enjarai.trickster.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.cca.ModEntityComponents;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix4f;

import java.util.Comparator;

public class BarsRenderer {
    public static final Identifier BAR_TEXTURE = Trickster.id("bar/bar");
    public static final Identifier BAR_BACKGROUND_TEXTURE = Trickster.id("bar/bar_background");

    private static final Random colorsRandom = new LocalRandom(0xba115);

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        var player = MinecraftClient.getInstance().player;

        if (player != null) {
            var barsComponent = player.getComponent(ModEntityComponents.BARS);
            var bars = barsComponent.getBars();

            context.getMatrices().push();

            if (Trickster.CONFIG.barsHorizontal()) {
                context.getMatrices().translate(context.getScaledWindowWidth() - 64, context.getScaledWindowHeight(), 0);
                context.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90));
                context.getMatrices().translate(-context.getScaledWindowWidth(), -context.getScaledWindowHeight(), 0);
            }

            int i = 0;
            for (var entry : bars.int2ObjectEntrySet().stream().sorted(Comparator.comparingInt(Int2ObjectMap.Entry::getIntKey)).toList()) {
                drawBar(context, i, entry.getIntKey(), 1, BAR_BACKGROUND_TEXTURE);
                drawBar(context, i, entry.getIntKey(), entry.getValue().fill, BAR_TEXTURE);

                i++;
            }

            context.getMatrices().pop();
        }
    }

    private static void drawBar(DrawContext context, int index, int id, double fill, Identifier texture) {
        var sprite = MinecraftClient.getInstance().getGuiAtlasManager().getSprite(texture);
        var xOffset = index * 8;
        fill = Math.clamp(fill, 0d, 1d);

        colorsRandom.setSeed(id);

        drawTexturedQuad(
                context,
                sprite.getAtlasId(),
                context.getScaledWindowWidth() - 2 - 5 - xOffset,
                context.getScaledWindowWidth() - 2 - xOffset,
                context.getScaledWindowHeight() - 2 - (int) (61 * fill),
                context.getScaledWindowHeight() - 2,
                0,
                sprite.getFrameU(0),
                sprite.getFrameU(1),
                sprite.getFrameV((float) ((int) ((1 - fill) * 61)) / 61),
                sprite.getFrameV(1),
                colorsRandom.nextFloat(),
                colorsRandom.nextFloat(),
                colorsRandom.nextFloat(),
                1
        );
    }

    private static void drawTexturedQuad(
            DrawContext context, Identifier texture, int x1, int x2, int y1, int y2, int z,
            float u1, float u2, float v1, float v2, float red, float green, float blue, float alpha
    ) {
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        RenderSystem.enableBlend();
        Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(matrix4f, (float)x1, (float)y1, (float)z).texture(u1, v1).color(red, green, blue, alpha);
        bufferBuilder.vertex(matrix4f, (float)x1, (float)y2, (float)z).texture(u1, v2).color(red, green, blue, alpha);
        bufferBuilder.vertex(matrix4f, (float)x2, (float)y2, (float)z).texture(u2, v2).color(red, green, blue, alpha);
        bufferBuilder.vertex(matrix4f, (float)x2, (float)y1, (float)z).texture(u2, v1).color(red, green, blue, alpha);
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();
    }
}
