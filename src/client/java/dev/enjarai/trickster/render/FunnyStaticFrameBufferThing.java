package dev.enjarai.trickster.render;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.event.WindowResizeCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;

import java.util.function.Supplier;

public class FunnyStaticFrameBufferThing {
    public static final Supplier<Framebuffer> THING = Suppliers.memoize(() -> {
        var window = MinecraftClient.getInstance().getWindow();
        var buffer = new SimpleFramebuffer(window.getWidth(), window.getHeight(), true, MinecraftClient.IS_SYSTEM_MAC);
        WindowResizeCallback.EVENT.register((client, window1) -> buffer.resize(window1.getWidth(), window1.getHeight(), MinecraftClient.IS_SYSTEM_MAC));
        return buffer;
    });

    public static void drawFunnily(MatrixStack matrices, float a, float r, float g, float b) {
        var window = MinecraftClient.getInstance().getWindow();
        var buffer = RenderSystem.renderThreadTesselator().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        var matrix = matrices.peek().getPositionMatrix();

        buffer.vertex(matrix, 0, window.getScaledHeight(), 0).texture(0, 0).color(r, g, b, a);
        buffer.vertex(matrix, window.getScaledWidth(), window.getScaledHeight(), 0).texture(1, 0).color(r, g, b, a);
        buffer.vertex(matrix, window.getScaledWidth(), 0, 0).texture(1, 1).color(r, g, b, a);
        buffer.vertex(matrix, 0, 0, 0).texture(0, 1).color(r, g, b, a);

        RenderSystem.setShaderTexture(0, THING.get().getColorAttachment());
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        BufferRenderer.drawWithGlobalProgram(buffer.end());
    }
}
