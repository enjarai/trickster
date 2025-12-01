package dev.enjarai.trickster.render.ward;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.ward.SimpleCubicWard;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

public class SimpleCubicWardRenderer implements WardRenderer<SimpleCubicWard> {
    static final Identifier TEXTURE = Trickster.id("textures/wards/simple_cubic.png");
    static final float QUARTER = (float) (Math.PI / 2f);

    @Override
    public void render(SimpleCubicWard ward, @Nullable SimpleCubicWard lastWard, WorldRenderContext context, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float tickDelta) {
        var buffer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(TEXTURE));
        var camPos = context.camera().getPos();
        var area = ward.area;
        var dimensions = area.getDimensions();
        var rotation = new Quaternionf();
        var color = ward.color(context.world());

        matrices.push();
        matrices.translate(
                -camPos.getX(),
                -camPos.getY(),
                -camPos.getZ()
        );
        matrices.translate(
                (area.getMinX() + area.getMaxX()) / 2f,
                (area.getMinY() + area.getMaxY()) / 2f,
                (area.getMinZ() + area.getMaxZ()) / 2f
        );
        matrices.scale(
                dimensions.getX() / 2f,
                dimensions.getY() / 2f,
                dimensions.getZ() / 2f
        );

        face(matrices, buffer, rotation, color, dimensions.getX(), dimensions.getY());
        face(matrices, buffer, rotation.rotateLocalY(QUARTER), color, dimensions.getZ(), dimensions.getY());
        face(matrices, buffer, rotation.rotateLocalY(QUARTER), color, dimensions.getX(), dimensions.getY());
        face(matrices, buffer, rotation.rotateLocalY(QUARTER), color, dimensions.getZ(), dimensions.getY());
        face(matrices, buffer, rotation.rotateLocalZ(QUARTER), color, dimensions.getZ(), dimensions.getX());
        face(matrices, buffer, rotation.rotateLocalZ(QUARTER * 2f), color, dimensions.getZ(), dimensions.getX());

        matrices.pop();
    }

    private void face(MatrixStack matrices, VertexConsumer buffer, Quaternionf rotation, int color, float sizeX, float sizeY) {
        matrices.push();
        matrices.multiply(rotation);
        matrices.translate(0f, 0f, 1.0001f);

        vertex(matrices, buffer, color, sizeX, sizeY, -1f, -1f);
        vertex(matrices, buffer, color, sizeX, sizeY, 1f, -1f);
        vertex(matrices, buffer, color, sizeX, sizeY, 1f, 1f);
        vertex(matrices, buffer, color, sizeX, sizeY, -1f, 1f);

        matrices.pop();
    }

    private void vertex(MatrixStack matrices, VertexConsumer buffer, int color, float sizeX, float sizeY, float offsetX, float offsetY) {
        var entry = matrices.peek();
        buffer
                .vertex(entry.getPositionMatrix(), offsetX, offsetY, 0f)
                .color(color)
                .texture((offsetX + 1) * sizeX / 2, (offsetY + 1) * sizeY / 2)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                .normal(entry, 0f, 0f, 1f);
    }
}
